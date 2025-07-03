CREATE TABLE `user` (
  `id` SERIAL COMMENT 'User ID',
  `username` VARCHAR(40) NOT NULL COMMENT 'Unique username',
  `password` VARCHAR(128) NOT NULL COMMENT 'Encrypted password',
  `email` VARCHAR(40) COMMENT 'Email address',
  `phone` VARCHAR(40) COMMENT 'Phone number',
  `nickname` VARCHAR(40) NOT NULL COMMENT 'User nickname',
  `avatar_file_id` BIGINT COMMENT 'File ID of the avatar image',
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'If the user is enabled',
  `last_login_time` TIMESTAMP COMMENT 'Last login time',
  `last_login_ip` VARCHAR(20) COMMENT 'Last login IP address',
  `preference` JSON COMMENT 'User preference settings in JSON format',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_USERNAME` (`username`)
) COMMENT='User info table';

CREATE TABLE `project` (
  `id` SERIAL COMMENT 'Project ID',
  `name` VARCHAR(128) NOT NULL COMMENT 'Project name',
  `code` VARCHAR(20) NOT NULL COMMENT 'Project code, 3-8 upper case letters, will be used to generate issue code, e.g. GGSM-1',
  `avatar_file_id` BIGINT COMMENT 'File ID of the avatar image',
  `description` TEXT COMMENT 'Project description',
  `last_issue_seq` BIGINT NOT NULL DEFAULT 0 COMMENT 'The last issue sequence number, will be used to generate the next new issue code',
  `start_date` TIMESTAMP COMMENT 'Project start time',
  `end_date` TIMESTAMP COMMENT 'Project end time',
  `deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Flag of deletion',
  `archived` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Flag of archiving',
  `time_tracking_enabled` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Flag to enable issue time tracking',
  `file_count` BIGINT NOT NULL DEFAULT 0 COMMENT 'Total file count in the project',
  `total_file_size` BIGINT NOT NULL DEFAULT 0 COMMENT 'Total file size in bytes',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_CREATOR_ID` (`created_by_user_id`)
) COMMENT='Project table';

CREATE TABLE `project_member` (
  `id` SERIAL COMMENT 'Project member ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role` ENUM('OWNER','DEVELOPER','GUEST') NOT NULL DEFAULT 'GUEST' COMMENT 'Member role',
  `join_channel` ENUM('CREATOR','MANUAL','INVITATION') COMMENT 'How the user joined the project',
  `invitation_id` BIGINT COMMENT 'The invitation ID if the user joined via an invitation',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_PROJECT_USER` (`project_id`,`user_id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_USER_ID` (`user_id`)
) COMMENT='Project-User relation table';

CREATE TABLE `invitation` (
  `id` SERIAL COMMENT 'Project invitation ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `code` VARCHAR(45) NOT NULL COMMENT 'Invitation code',
  `invitation_type` ENUM('DEVELOPER','GUEST') NOT NULL DEFAULT 'GUEST' COMMENT 'Invitation type',
  `valid_days` INT COMMENT 'Valid days of the invitation',
  `expire_time` TIMESTAMP COMMENT 'Expiration time of the invitation',
  `enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'If the invitation is enabled',
  `join_count` INT NOT NULL DEFAULT 0 COMMENT 'User count who joined the project via this invitation',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_CODE` (`code`)
) COMMENT='Project invitation table';

CREATE TABLE `sprint` (
  `id` SERIAL COMMENT 'Project sprint ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID this sprint belongs to',
  `name` VARCHAR(255) NOT NULL COMMENT 'Sprint name',
  `goal` TEXT COMMENT 'Sprint goal',
  `backlog` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'If this is the project backlog',
  `start_date` TIMESTAMP COMMENT 'Sprint start time',
  `end_date` TIMESTAMP COMMENT 'Sprint end time',
  `total_issue_count` BIGINT DEFAULT 0 COMMENT 'Total issue in this sprint',
  `done_issue_count` BIGINT DEFAULT 0 COMMENT 'Total issue in status of DONE',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`)
) COMMENT='Project sprint table';

CREATE TABLE `issue_group` (
  `id` SERIAL COMMENT 'Issue group ID',
  `label` VARCHAR(50) NOT NULL COMMENT 'Group label, e.g. TODO, IN_PROGRESS, DONE',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `built_in` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'If this is a built-in group, built-in groups cannot be deleted',
  `seq` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Group order sequence',
  `status` ENUM('TO_DO','IN_PROGRESS','DONE') NOT NULL COMMENT 'Group  status, multiple groups can be in status of IN_PROGRESS',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`)
) COMMENT 'An issue group is a vertical list of issues in a project, e.g. TODO, IN_PROGRESS, DONE';

CREATE TABLE `component` (
  `id` SERIAL COMMENT 'Project component ID',
  `name` VARCHAR(256) NOT NULL COMMENT 'Component name',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID this component belongs to',
  `parent_id` BIGINT COMMENT 'ID of parent component if has',
  `path` VARCHAR(2048) NOT NULL DEFAULT '/' COMMENT 'Path from the root, e.g. /938372/7635637/763602/',
  `seq` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Sequence of the component',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT` (`project_id`),
  KEY `IDX_PARENT_ID` (`parent_id`)
) COMMENT='Project component table';

CREATE TABLE `doc` (
  `id` SERIAL COMMENT 'Doc ID',
  `project_id` BIGINT COMMENT 'Project ID if this doc belongs to a project',
  `name` VARCHAR(128) NOT NULL COMMENT 'Doc name',
  `content` LONGTEXT COMMENT 'Doc content',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_CREATOR_ID` (`created_by_user_id`),
  KEY `IDX_UPDATER_ID` (`updated_by_user_id`)
) COMMENT='Project doc table';

CREATE TABLE `file` (
  `id` SERIAL COMMENT 'File ID',
  `project_id` BIGINT COMMENT 'Project ID if this file belongs to a project',
  `name` VARCHAR(128) NOT NULL COMMENT 'File name',
  `folder` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'If this is a folder',
  `parent_id` BIGINT COMMENT 'Parent folder ID',
  `issue_id` BIGINT COMMENT 'Issue ID if this file is attached to an issue',
  `full_path` VARCHAR(256) COMMENT 'Full path of the file on object storage service, including file name',
  `url_prefix` VARCHAR(512) COMMENT 'URL prefix, e.g. https://oss.example.com/bucket/abc/ for cloud storage, or /lfs/ for local storage',
  `type` ENUM('IMAGE','AUDIO','VIDEO','WORD','PDF','PPT','EXCEL','ZIP','RAR','OTHER') COMMENT 'File type',
  `target_type` ENUM('PROJECT_FILE', 'PROJECT_AVATAR', 'USER_AVATAR', 'ISSUE_ATTACHMENT', 'TEXT_EDITOR') COMMENT 'Target type',
  `size` BIGINT COMMENT 'File size in bytes',
  `storage_provider` ENUM('LOCAL', 'ALIYUN') COMMENT 'Underlying storage provider',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_PARENT_ID` (`parent_id`),
  KEY `IDX_CREATOR_ID` (`created_by_user_id`),
  KEY `IDX_UPDATER_ID` (`updated_by_user_id`)
) COMMENT='File table';

CREATE TABLE `tag` (
  `id` SERIAL COMMENT 'Tag ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID this tag belongs to',
  `color` VARCHAR(45) COMMENT 'Color of the tag, e.g. #09c32e',
  `name` VARCHAR(45) NOT NULL COMMENT 'Name of the tag',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`)
) COMMENT='Tag table';

CREATE TABLE `issue` (
  `id` SERIAL COMMENT 'Issue ID',
  `name` VARCHAR(1024) NOT NULL COMMENT 'Issue title',
  `code` VARCHAR(30) NOT NULL COMMENT 'Unique issue code within a project',
  `description` TEXT COMMENT 'Issue description',
  `type` ENUM('REQUIREMENT','TASK','BUG') NOT NULL COMMENT 'Issue type',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `component_id` BIGINT COMMENT 'Component ID',
  `sprint_id` BIGINT NOT NULL COMMENT 'Sprint ID',
  `owner_id` BIGINT COMMENT 'To which user this issue is assigned',
  `issue_group_id` BIGINT COMMENT 'Issue group ID',
  `priority` ENUM('TRIVIAL','LOW','NORMAL','MAJOR','CRITICAL') NOT NULL DEFAULT 'NORMAL' COMMENT 'Priority of the issue',
  `seq` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Order sequence of the issue in the group',
  `story_points` FLOAT COMMENT 'Estimated story points',
  `completed_time` TIMESTAMP COMMENT 'Time when the issue is completed',
  `due_time` TIMESTAMP COMMENT 'Issue due time',
  `estimated_hours` FLOAT COMMENT 'Estimation in hours',
  `actual_hours` FLOAT COMMENT 'Actual spent hours',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_SPRINT_ID` (`sprint_id`),
  KEY `IDX_OWNER_ID` (`owner_id`),
  KEY `IDX_GROUP_ID` (`issue_group_id`),
  KEY `IDX_COMPONENT_ID` (`component_id`),
  UNIQUE KEY `UNQ_ISSUE_CODE` (`project_id`, `code`)
) COMMENT='Issue table';

CREATE TABLE `comment` (
  `id` SERIAL COMMENT 'Issue comment ID',
  `issue_id` BIGINT NOT NULL COMMENT 'Issue ID',
  `content` VARCHAR(2048) NOT NULL COMMENT 'Comment content',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_ISSUE_ID` (`issue_id`)
) COMMENT='Issue comment table';

CREATE TABLE `history` (
  `id` SERIAL COMMENT 'Issue history ID',
  `entity_type` VARCHAR(128) NOT NULL COMMENT 'Entity class name',
  `entity_id` BIGINT NOT NULL COMMENT 'Entity ID',
  `action_type` ENUM('CREATE','UPDATE','DELETE') NOT NULL COMMENT 'Action type',
  `details` TEXt COMMENT 'Details of the change',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_TYPE_AND_ID` (`entity_type`,`entity_id`)
) COMMENT='Entity changes history table';

CREATE TABLE `issue_link` (
  `issue_id` BIGINT NOT NULL COMMENT 'Issue ID',
  `linked_issue_id` BIGINT NOT NULL COMMENT 'The linked issue ID',
  PRIMARY KEY (`issue_id`,`linked_issue_id`),
  KEY `IDX_ISSUE_ID` (`issue_id`)
) COMMENT='Issue-Issue relationship';

CREATE TABLE `issue_tag` (
  `issue_id` BIGINT NOT NULL COMMENT 'Issue ID',
  `tag_id` BIGINT NOT NULL COMMENT 'Tag ID',
  PRIMARY KEY (`issue_id`,`tag_id`),
  KEY `IDX_ISSUE_ID` (`issue_id`)
) COMMENT='Issue-Tag relationship';

CREATE TABLE `issue_filter` (
  `id` SERIAL COMMENT 'Issue filter ID',
  `name` VARCHAR(128) NOT NULL COMMENT 'Filter name',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID this filter belongs to',
  `keyword` VARCHAR(128) COMMENT 'Search keyword',
  `seq` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Filter order sequence',
  `sprint_ids` JSON COMMENT 'List of sprint ID',
  `group_ids` JSON COMMENT 'List of issue group ID',
  `component_ids` JSON COMMENT 'List of component ID',
  `types` JSON COMMENT 'Issue types',
  `priorities` JSON COMMENT 'Priorities',
  `tag_ids` JSON COMMENT 'List of tag ID',
  `owner_ids` JSON COMMENT 'List of issue assignees ID',
  `orders` JSON COMMENT 'Sorting orders',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT NOT NULL COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT NOT NULL COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`)
) COMMENT='Issue filter table';

CREATE TABLE `sprint_issue_count` (
  `sprint_id` BIGINT NOT NULL COMMENT 'Sprint ID',
  `daily_counts` LONGTEXT COMMENT 'Details of daily statistics',
  PRIMARY KEY (`sprint_id`)
) COMMENT='Daily sprint statistics for charts';