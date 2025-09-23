CREATE TABLE `user_binding` (
  `id` SERIAL COMMENT 'Binding ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `ext_user_id` VARCHAR(100) NOT NULL COMMENT 'User ID in 3rd-party system',
  `provider` VARCHAR(50) NOT NULL COMMENT '3rd-party provider name',
  `union_id` VARCHAR(100) COMMENT 'Union ID in 3rd-party system',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_USER_ID` (`user_id`),
  KEY `IDX_EXT_USER_ID` (`ext_user_id`),
  UNIQUE KEY `UNQ_BINDING` (`user_id`, `ext_user_id`),
  UNIQUE KEY `UNQ_EXT_USER` (`ext_user_id`, `provider`)
) COMMENT='User 3rd-party OAuth binding table';

ALTER TABLE `user` CHANGE `password` `password` VARCHAR(128) COMMENT 'Encrypted password';