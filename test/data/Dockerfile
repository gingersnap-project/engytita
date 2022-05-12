# We must create a custom image as the aiport-codes.sql is too large to use in a ConfigMap
FROM postgres:14.2
COPY ./*.sql /docker-entrypoint-initdb.d/
