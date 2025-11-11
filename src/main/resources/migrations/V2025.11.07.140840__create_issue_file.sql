CREATE TABLE `issue_file` (
  `issue_id` BIGINT NOT NULL COMMENT 'Issue ID',
  `file_id` BIGINT NOT NULL COMMENT 'File ID',
  PRIMARY KEY (`issue_id`, `file_id`)
) COMMENT='Issue file link table';

INSERT INTO `issue_file` (`issue_id`, `file_id`)
  SELECT `issue_id`, `id` FROM `file` WHERE `issue_id` IS NOT NULL;

ALTER TABLE `file` DROP `issue_id`;