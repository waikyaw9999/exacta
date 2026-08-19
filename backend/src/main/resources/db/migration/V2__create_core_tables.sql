-- Core domain schema for time tracking and billing.

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(32)  NOT NULL,
    hourly_rate     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'MEMBER')),
    CONSTRAINT ck_users_hourly_rate CHECK (hourly_rate >= 0)
);

CREATE TABLE clients (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(180) NOT NULL,
    contact_email   VARCHAR(255),
    company         VARCHAR(180) NOT NULL
);

CREATE TABLE projects (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(180) NOT NULL,
    client_id       BIGINT NOT NULL REFERENCES clients (id) ON DELETE RESTRICT,
    status          VARCHAR(32) NOT NULL,
    CONSTRAINT ck_projects_status CHECK (status IN ('ACTIVE', 'ON_HOLD', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX idx_projects_client_id ON projects (client_id);

CREATE TABLE time_entries (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    project_id          BIGINT NOT NULL REFERENCES projects (id) ON DELETE RESTRICT,
    start_time          TIMESTAMPTZ NOT NULL,
    end_time            TIMESTAMPTZ,
    duration_minutes    INTEGER,
    description         VARCHAR(1000),
    is_billable         BOOLEAN NOT NULL DEFAULT TRUE,
    status              VARCHAR(32) NOT NULL,
    CONSTRAINT ck_time_entries_status CHECK (status IN ('RUNNING', 'STOPPED', 'SUBMITTED', 'BILLED')),
    CONSTRAINT ck_time_entries_duration CHECK (duration_minutes IS NULL OR duration_minutes >= 0),
    CONSTRAINT ck_time_entries_window CHECK (end_time IS NULL OR end_time >= start_time)
);

CREATE INDEX idx_time_entries_user_id ON time_entries (user_id);
CREATE INDEX idx_time_entries_project_id ON time_entries (project_id);
CREATE INDEX idx_time_entries_start_time ON time_entries (start_time);
CREATE UNIQUE INDEX uq_time_entries_one_running_per_user
    ON time_entries (user_id)
    WHERE status = 'RUNNING';
