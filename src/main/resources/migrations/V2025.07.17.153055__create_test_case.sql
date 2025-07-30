CREATE TABLE `test_case` (
  `id` SERIAL COMMENT 'Test case ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `code` BIGINT NOT NULL COMMENT 'Test case code',
  `latest_details_id` BIGINT COMMENT 'Latest test case details ID',
  `latest_version` INT COMMENT 'Latest version of the test case',
  `deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Flag of deletion',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  UNIQUE KEY `UNQ_TEST_CASE_CODE` (`project_id`, `code`)
) COMMENT='Test case table';

CREATE TABLE `test_case_details` (
  `id` SERIAL COMMENT 'Test case details ID',
  `test_case_id` BIGINT NOT NULL COMMENT 'Test case ID',
  `component_id` BIGINT COMMENT 'Component ID',
  `name` VARCHAR(1024) NOT NULL COMMENT 'Test case name',
  `description` TEXT COMMENT 'Test case description',
  `type` VARCHAR(64) COMMENT 'Test case type',
  `priority` ENUM('TRIVIAL','LOW','NORMAL','MAJOR','CRITICAL') NOT NULL DEFAULT 'NORMAL' COMMENT 'Priority of the test case',
  `version` INT NOT NULL DEFAULT 1 COMMENT 'Test case details version',
  `preconditions` TEXT COMMENT 'Preconditions for the test case',
  `steps` JSON COMMENT 'Test case steps in JSON format',
  `owner_id` BIGINT COMMENT 'Owner user ID',
  `automated` BOOLEAN COMMENT 'If this is a automated test case',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_TEST_CASE_ID` (`test_case_id`),
  KEY `IDX_COMPONENT_ID` (`component_id`),
  UNIQUE KEY `UNQ_TEST_CASE_VERSION` (`test_case_id`, `version`)
) COMMENT='Test case details table';