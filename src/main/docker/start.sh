#!/bin/bash


PORT="$1"
DATABASE_URL="$2"
BOT_TOKEN="$3"
BOT_REPO_URL="$4"
BOT_DONATE_URL="$5"
BOT_REST_URL="$6"

regex="postgres:\/\/(\w+):(\w+)@(.+):(.+)\/(\w+)"

[[ $DATABASE_URL =~ $regex ]]

POSTGRESQL_USERNAME="${BASH_REMATCH[1]}"
POSTGRESQL_PASSWORD="${BASH_REMATCH[2]}"
POSTGRESQL_HOST="${BASH_REMATCH[3]}"
POSTGRESQL_PORT="${BASH_REMATCH[4]}"
DB_NAME="${BASH_REMATCH[5]}"


#echo "$POSTGRESQL_USERNAME"
#echo "$POSTGRESQL_PASSWORD"
#echo "$POSTGRESQL_HOST"
#echo "$POSTGRESQL_PORT"
#echo "$DB_NAME"

java \
  -Xmx490m \
  -Dquarkus.http.host=0.0.0.0 \
  -Dquarkus.http.port="${PORT}" \
  -DPOSTGRESQL_HOST="${POSTGRESQL_HOST}" \
  -DPOSTGRESQL_PORT="${POSTGRESQL_PORT}" \
  -DDB_NAME="${DB_NAME}" \
  -Dquarkus.datasource.username="${POSTGRESQL_USERNAME}" \
  -Dquarkus.datasource.password="${POSTGRESQL_PASSWORD}" \
  -Dbot.token="${BOT_TOKEN}" \
  -Dbot.repo.url="${BOT_REPO_URL}" \
  -Dbot.donate.url="${BOT_DONATE_URL}" \
  -Dquarkus.scheduler.enabled=true \
  -Dbot.cleanup.job.cron.expr="0 0 */12 * * ?" \
  -Dall-you-can-eat-bot-rest-client/mp-rest/url="${BOT_REST_URL}" \
  -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -jar /deployments/quarkus-run.jar
