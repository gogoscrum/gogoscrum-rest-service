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

CREATE TABLE `test_case_file` (
  `test_case_id` BIGINT NOT NULL COMMENT 'Test case ID',
  `file_id` BIGINT NOT NULL COMMENT 'File ID',
  PRIMARY KEY (`test_case_id`, `file_id`)
) COMMENT='Test case attachment table';

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

CREATE TABLE `test_run` (
  `id` SERIAL COMMENT 'Test run ID',
  `project_id` BIGINT NOT NULL COMMENT 'Project ID',
  `test_case_id` BIGINT NOT NULL COMMENT 'Test case ID',
  `test_case_details_id` BIGINT NOT NULL COMMENT 'Test case details ID',
  `test_case_version` INT NOT NULL DEFAULT 1 COMMENT 'Version of the test case',
  `test_plan_id` BIGINT COMMENT 'Test plan ID, optional',
  `step_results` JSON COMMENT 'Actual result of all steps in JSON format',
  `status` ENUM('SKIPPED','BLOCKED','SUCCESS','FAILED') COMMENT 'Status of the test run',
  `result` TEXT COMMENT 'Test case execution result remark',
  `created_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Updated time',
  `created_by_user_id` BIGINT COMMENT 'Creator user ID',
  `updated_by_user_id` BIGINT COMMENT 'Updater user ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROJECT_ID` (`project_id`),
  KEY `IDX_TEST_CASE_ID` (`test_case_id`),
  KEY `IDX_TEST_PLAN_ID` (`test_plan_id`)
) COMMENT='Test case execution record table';

CREATE TABLE `test_run_file` (
  `test_run_id` BIGINT NOT NULL COMMENT 'Test run ID',
  `file_id` BIGINT NOT NULL COMMENT 'File ID',
  PRIMARY KEY (`test_run_id`, `file_id`)
) COMMENT='Test run attachment table';

ALTER TABLE `file` CHANGE `target_type` `target_type` ENUM('PROJECT_FILE', 'PROJECT_AVATAR', 'USER_AVATAR',
'ISSUE_ATTACHMENT', 'TEXT_EDITOR', 'TEST_CASE_ATTACHMENT', 'TEST_RUN_ATTACHMENT') COMMENT 'Target type';