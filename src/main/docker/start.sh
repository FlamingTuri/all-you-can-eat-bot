#!/bin/bash


PORT="$1"
DATABASE_URL="$2"
BOT_USERNAME="$3"
BOT_TOKEN="$4"
BOT_HOST="$5"
BOT_REPO_URL="$6"
BOT_DONATE_URL="$7"
BOT_REST_URL="$8"

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
  -DPOSTGRESQL_USERNAME="${POSTGRESQL_USERNAME}" \
  -DPOSTGRESQL_PASSWORD="${POSTGRESQL_PASSWORD}" \
  -DBOT_USERNAME="${BOT_USERNAME}" \
  -DBOT_TOKEN="${BOT_TOKEN}" \
  -DBOT_HOST="${BOT_HOST}" \
  -DBOT_REPO_URL="${BOT_REPO_URL}" \
  -DBOT_DONATE_URL="${BOT_DONATE_URL}" \
  -DCRON_ENABLED=true \
  -DKEEP_ALIVE_JOB_CRON_EXPR="0 */45 * * * ?" \
  -DCLEANUP_JOB_CRON_EXPR="0 0 */12 * * ?" \
  -Dall-you-can-eat-bot-rest-client/mp-rest/url="${BOT_REST_URL}" \
  -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -jar /deployments/quarkus-run.jar
