# all-you-can-eat-bot

```bash
docker pull postgres
docker run --rm=true --name postgres-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=docker \
  -e POSTGRES_DB=order \
  -p 5432:5432 \
  -d postgres
```
