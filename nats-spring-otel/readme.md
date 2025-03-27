# Setup

```
NATS_GIRLS_STREAM_NAME=my-girls
NATS_GIRLS_CONSUMER_NAME=girl_consumer
NATS_GIRLS_SUBJECT_BASE=girls
```

# DDL

```sql
CREATE TABLE girls_rating (
    rating_id SERIAL PRIMARY KEY,
    tracing_context TEXT,
    girl_id SMALLINT,
    name VARCHAR NOT NULL,
    rating NUMERIC(3, 2)
);
```