ALTER TABLE `user` ADD `admin` BOOLEAN NOT NULL DEFAULT FALSE
    COMMENT 'If the user is platform admin' AFTER `enabled`;