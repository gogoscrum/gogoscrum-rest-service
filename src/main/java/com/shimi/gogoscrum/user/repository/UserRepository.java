package com.shimi.gogoscrum.user.repository;

import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends GeneralRepository<User> {
    User findByUsername(String username);

    /**
     * Find users who are used to work with the current user in the same projects,
     * filtered by name (nickname or username).
     */
    @Query("select distinct u from User u where u in (select p.user from ProjectMember p " +
            "where p.project in (select p1.project from ProjectMember p1 where p1.user = :currentUser " +
            "and p1.project.deleted = false) and " +
            "(p.user.nickname like %:name% or p.user.username like %:name%) and p.project.deleted = false) order by u.id")
    Page<User> findProjectMates(User currentUser, Pageable pageable, String name);

    /**
     * Find users who are used to work with the current user in the same projects.
     */
    @Query("select distinct u from User u where u in (select p.user from ProjectMember p " +
            "where p.project in (select p1.project from ProjectMember p1 where p1.user = :currentUser " +
            "and p1.project.deleted = false) and "+
            "p.project.deleted = false) order by u.id")
    Page<User> findProjectMates(User currentUser, Pageable pageable);
}
