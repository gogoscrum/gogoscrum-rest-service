CREATE TABLE `test_plan` (
  `id` SERIAL COMMENT 'Test plan ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `name` VARCHAR(1024) NOT NULL COMMENT 'Test plan name',
  `start_date` TIMESTAMP COMMENT 'Plan start time',
  `end_date` TIMESTAMP COMMENT 'Plan end time',
  `type` VARCHAR(64) COMMENT 'Test case type',
  `owner_id` BIGINT COMMENT 'Owner user ID',
  `deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Flag of deletion',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`)
) COMMENT='Test plan table';

CREATE TABLE `test_plan_item` (
  `id` SERIAL COMMENT 'Test plan item ID',
  `test_plan_id` BIGINT NOT NULL COMMENT 'Test plan ID',
  `test_case_id` BIGINT NOT NULL COMMENT 'Test case ID',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PLAIN_ID` (`test_plan_id`),
  KEY `IDX_CASE_ID` (`test_case_id`),
  UNIQUE KEY `UNQ_TEST_PLAN_ITEM` (`test_plan_id`, `test_case_id`)
) COMMENT='Test plan item table';