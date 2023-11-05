create table if not exists t_metrics
(
    c_id         uuid default random_uuid() primary key,
    c_indicator  varchar(255) not null,
    c_value      varchar(255) not null,
    c_type       varchar(255) not null,
    c_recorded_At timestamp    not null
);