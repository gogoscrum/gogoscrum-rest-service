package com.shimi.gogoscrum.file.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.FileUtil;
import com.shimi.gogoscrum.common.util.PermissionUtil;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileFilter;
import com.shimi.gogoscrum.file.model.FileUploadToken;
import com.shimi.gogoscrum.file.model.TargetType;
import com.shimi.gogoscrum.file.repository.FileRepository;
import com.shimi.gogoscrum.file.repository.FileSpecs;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.EntityNotFoundException;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service implementation for managing files. Multiple file storage providers can be used,
 * with the default built-in local storage service. If multiple file storage plugins are
 * available, the first one (which annotated with the lowest ordinal, e.g. {@code @Extension(ordinal = 0))})
 * will be used as the major active storage provider. All new files will be uploaded to this
 * active provider. Meanwhile, the deletion of existing files will be handled by the old
 * original storage provider that was used when the file was uploaded.
 */
@Service
public class FileServiceImpl extends BaseServiceImpl<File, FileFilter> implements FileService {
    public static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    @Autowired
    private FileRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileStorage fileStorage;
    @Autowired
    private PluginManager pluginManager;
    private final Map<String , FileStorage> fileStorageServices = new HashMap<>();

    @PostConstruct
    private void initPlugins() {
        // Put the default file storage (LOCAL) into the map
        fileStorageServices.put(fileStorage.getProvider(), fileStorage);

        List<FileStorage> fileStoragePlugins = pluginManager.getExtensions(FileStorage.class);

        if (!fileStoragePlugins.isEmpty()) {
            // put all file storage plugins into the map
            fileStorageServices.putAll(fileStoragePlugins.stream()
                    .collect(Collectors.toMap(FileStorage::getProvider, fs -> fs)));

            fileStorage = fileStoragePlugins.getFirst();

            if (fileStoragePlugins.size() > 1) {
                log.warn("Multiple file storage plugins found. The first one {} will be used as the storage provider", fileStorage.getProvider());
            } else {
                log.info("File storage plugin {} found and will be used as storage provider", fileStorage.getProvider());
            }
        }
    }

    @Override
    protected FileRepository getRepository() {
        return repository;
    }

    @Override
    public FileUploadToken generateUploadToken(String originalFileName, Long projectId, TargetType targetType) {
        if (targetType == null) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Target type is required", HttpStatus.BAD_REQUEST);
        }

        if (!targetType.equals(TargetType.USER_AVATAR) && projectId == null) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Project ID is required for this target type", HttpStatus.BAD_REQUEST);
        }

        if (targetType.equals(TargetType.USER_AVATAR) && projectId != null) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Project ID should not be provided for user avatar uploads", HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(originalFileName)) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "File name is empty", HttpStatus.BAD_REQUEST);
        }

        if (projectId != null) {
            Project project = projectService.get(projectId);
            log.debug("project loaded: {}, {}", project, project.getDebugInfo());
            ProjectMemberUtils.checkDeveloper(project, getCurrentUser());
        }

        String targetFileName = FileUtil.generateRandomFileName(FileUtil.getExt(originalFileName));
        String subPath = "/" + targetType.name().toLowerCase();
        String path = projectId != null ? "/projects/" + projectId + subPath : subPath;

        FileUploadToken token = fileStorage.generateUploadToken(originalFileName, path, targetFileName);
        token.setPath(path);
        token.setSourceFileName(originalFileName);
        token.setTargetFileName(targetFileName);
        token.setProjectId(projectId);
        token.setTargetType(targetType);

        if (log.isDebugEnabled()) {
            log.debug("Generated file upload token: {}", token);
        }
        return token;
    }

    @Override
    public String upload(MultipartFile multipartFile, String path, String targetFileName) {
        if (multipartFile.isEmpty()) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "File is empty", HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(path)) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Upload path is required", HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(targetFileName)) {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Target file name is required", HttpStatus.BAD_REQUEST);
        }

        try {
            return fileStorage.upload(multipartFile.getInputStream(), path, targetFileName);
        } catch (IOException e) {
            throw new BaseServiceException("fileUploadError", "Failed to save uploaded file: " + multipartFile.getOriginalFilename(), HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public File get(Long id) {
        File file = super.get(id);
        if (file.getProjectId() != null) {
            ProjectMemberUtils.checkMember(projectService.get(file.getProjectId()), getCurrentUser());
        }
        return file;
    }

    @Override
    public void deleteByPath(String path) {
        File file = repository.findByFullPath(path);
        if (file == null) {
            throw new EntityNotFoundException("File not found by path " + path);
        }

        super.delete(file.getId());
    }

    @Override
    protected Specification<File> toSpec(FileFilter filter) {
        Specification<File> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = FileSpecs.projectIdEqual(filter.getProjectId());
        }

        if (filter.getParentId() != null) {
            Specification<File> parentIdEqual = FileSpecs.parentIdEqual(filter.getParentId());
            querySpec = Objects.isNull(querySpec) ? parentIdEqual : querySpec.and(parentIdEqual);
        } else if (filter.isRootLevel()) {
            Specification<File> parentIdNull = FileSpecs.parentIdNull();
            querySpec = Objects.isNull(querySpec) ? parentIdNull : querySpec.and(parentIdNull);
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            Specification<File> nameLike = FileSpecs.nameLike(filter.getKeyword());
            querySpec = Objects.isNull(querySpec) ? nameLike : querySpec.and(nameLike);
        }

        if (filter.getTargetType() != null) {
            Specification<File> targetTypeEqual = FileSpecs.targetTypeEqual(filter.getTargetType());
            querySpec = Objects.isNull(querySpec) ? targetTypeEqual : querySpec.and(targetTypeEqual);
        }

        return querySpec;
    }

    @Override
    protected void beforeCreate(File file) {
        if (file.getProjectId() != null) {
            ProjectMemberUtils.checkDeveloper(projectService.get(file.getProjectId()), getCurrentUser());
        }

        // If it's a folder, ensure the target type is PROJECT_FILE
        if (file.getFolder() && file.getTargetType() == null) {
            file.setTargetType(TargetType.PROJECT_FILE);
        }
    }

    @Override
    protected void beforeUpdate(Long id, File existingEntity, File newEntity) {
        if (existingEntity.getProjectId() != null) {
            ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getProjectId()),
                    getCurrentUser());
        }

        // Only file name and parent can be updated
        BeanUtils.copyProperties(existingEntity, newEntity, "name", "parent");
    }

    protected void beforeDelete(File file) {
        if (file.getProjectId() != null) {
            if (Objects.equals(file.getTargetType(), TargetType.PROJECT_AVATAR)) {
                ProjectMemberUtils.checkOwner(projectService.get(file.getProjectId()), getCurrentUser());
            } else {
                ProjectMemberUtils.checkDeveloper(projectService.get(file.getProjectId()), getCurrentUser());
            }
        } else {
            PermissionUtil.checkOwnership(file, getCurrentUser());
        }

        if (file.getFolder()) {
            List<File> children = this.findAllChildren(file);
            if (!children.isEmpty()) {
                children.forEach(child -> {
                    this.delete(child.getId());
                });

                log.info("Delete {} children of folder {}", children.size(), file);
            }
        }
    }

    private List<File> findAllChildren(File folder) {
        FileFilter filter = new FileFilter();
        filter.setParentId(folder.getId());
        Specification<File> querySpec = toSpec(filter);
        return this.repository.findAll(querySpec);
    }

    protected void afterDelete(File file) {
        if (StringUtils.hasText(file.getFullPath())) {
            if (Objects.equals(file.getStorageProvider(), fileStorage.getProvider())) {
                this.fileStorage.delete(file.getFullPath());
            } else if (fileStorageServices.containsKey(file.getStorageProvider())) {
                FileStorage oldFileStorage = fileStorageServices.get(file.getStorageProvider());
                oldFileStorage.delete(file.getFullPath());
            } else {
                log.warn("The old file storage service {} does not match current storage service {}, " +
                        "and the old storage service is not presented, skipping deletion from the storage.",
                        file.getStorageProvider(), fileStorage.getProvider());
            }
        }
    }
}
