CREATE TABLE `setting` (
  `id` SERIAL COMMENT 'Setting ID',
  `allow_signup` BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'If user self signup is allowed',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`)
) COMMENT='System setting table';

