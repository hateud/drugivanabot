from telethon import TelegramClient, events
from telegram import Bot
import json
import sys
import asyncio
userId = sys.argv[1]
api_id = sys.argv[2]
       
api_hash = sys.argv[3]
channels = sys.argv[4]
triggers = sys.argv[5]
user_client = TelegramClient('user_session', api_id, api_hash)


bot_token = '8426928345:AAEvVJclUt1YktYdO1aWNlxBZCly47yz_cM'

bot = Bot(bot_token)


@user_client.on(events.NewMessage(chats=channels))
async def handler(event):
    msg_text = getattr(event.message, 'message', '')
    if msg_text and any(trigger.lower() in msg_text.lower() for trigger in triggers):
        entity = await event.get_chat()
        username = getattr(entity, 'username', None)
    
        post_link = f"https://t.me/{username}/{event.message.id}"
        await bot.send_message(userId, f"Найден пост:\n{msg_text}\nСсылка: {post_link}")


async def main():
    await bot.send_message(
        userId,
        "Тестовое сообщение!\nЕсли вы видите это сообщение,\nзначит скрипт запущен и работает по конфигурации"
    )

    await user_client.start()
    print("Бот слушает каналы...")
    await user_client.run_until_disconnected()

if __name__ == "__main__":
    asyncio.run(main())