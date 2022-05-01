# all-you-can-eat-bot

Telegram bot that helps you manage orders for restaurants that use all-you-can-eat formula. The bot main aim is
registering a group chat orders and provide an order recap once everyone is done ordering.

## Commands

```bash
Commands supported by all you can eat bot:

Bot info commands
/start 
    - display the welcome message
/help 
    - display this help message

Group commands, should be used all in the same group to work
/createOrder {orderName}
    - create a new order
/joinOrder {orderName}
    - join an existing OPEN order
/leaveOrder {orderName}
    - leaves an OPEN order
/closeOrder {orderName}
    - close an order, preventing further modifications
/openOrder {orderName}
    - open a CLOSED order
/showOrder {orderName}
    - display a recap of an order
/blame {menuNumber} {orderName:}
    - search who ordered a dish

Unrestricted commands, can be used directly in the bot chat to avoid flooding groups with messages
/addDish {menuNumber} {quantity:1} {dishName:}
    - add a dish to your order
/nameDish {menuNumber} {dishName}
    - set or change the name of a dish
/removeDish {menuNumber} {quantityToRemove:all}
    - remove a dish from an OPEN order

Note: the value after ':' will be used when a command param has not been specified
```

### Usage example

```bash
# create a new order called 'my-order' for the current chat
/createOrder my-order
# join the order 'my-order'
/joinOrder my-order
# add menu number 6 one time (labelling it 'sashimi')
/addDish 6 1 sashimi
# add menu number 55 three times (labelling it 'nighiri')
/addDish 22 3 nighiri
# adds menu number 42 one time
/addDish 42
# labels menu number 42 as 'uramaki'
/nameDish 42 uramaki
# close the order 'my-order' to prevent further modifications
/closeOrder my-order
```

## Development

You'll need:

- java 11+
- docker

Setup docker postgres container:

```bash
docker pull postgres
# add --rm=true if you want to create a temporary container
docker run --name postgres-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=docker \
  -e POSTGRES_DB=order \
  -p 5432:5432 \
  -d postgres
docker start postgres-db
```

Run the application with:

```bash
./gradlew quarkusDev \
  -Dquarkus.profile=dev \ # enables detailed loggings
  -DBOT_TOKEN=<your-token> \
  -DCRON_ENABLED=true \ # enables db cleanup job
  --console=plain
```

Run tests with:

```bash
./gradlew test
```

## License

[GPL-3.0](LICENSE)
