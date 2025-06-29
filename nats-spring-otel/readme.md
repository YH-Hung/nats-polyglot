# Setup

```
KAFKA_GIRLS_TOPIC_NAME=my-girls
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

# Trace Context

## Properties

```
#Sun Jun 29 17:04:57 CST 2025\ntraceparent=00-2d85abbc9f992811fdd731501a6a472c-543f30fb6d58bf89-01\n
```
