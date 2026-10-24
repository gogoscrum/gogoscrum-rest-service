ALTER TABLE `doc` ADD `size` BIGINT NOT NULL DEFAULT 0 COMMENT 'Content size in UTF-8 bytes' AFTER `content`;

UPDATE `doc` SET `size` = IFNULL(LENGTH(`content`), 0);