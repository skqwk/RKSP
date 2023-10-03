CREATE TABLE T_MESSAGE (
       uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       body VARCHAR(255),
       author VARCHAR(255),
       channel VARCHAR(255),
       created_at timestamp
);