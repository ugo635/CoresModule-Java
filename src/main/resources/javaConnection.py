import discord
from discord import app_commands
from discord.ext import commands, tasks
import asyncio
import os
import sys
import time
import json as Json

class color:
    PURPLE = '\033[95m'
    CYAN = '\033[96m'
    DARKCYAN = '\033[36m'
    BLUE = '\033[94m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    END = '\033[0m'

intents = discord.Intents().all()

bot = commands.Bot(command_prefix='$', intents=intents)

intents.message_content = True
intents.guilds = True
intents.members = True

client = discord.Client(intents=intents)
tree = app_commands.CommandTree(client)

def print(s):
    with open("config/coresmodule/bot/bot.log", "a") as f:
        f.write(str(s) + "\n")


@bot.event
async def on_ready():
    print(f'{color.BOLD+color.GREEN}Logged in as {bot.user.name}{color.END}')
    try:
        synced = await bot.tree.sync()
        print(f"{color.BOLD+color.GREEN}Synced {len(synced)} commands :{color.END}")
        for s in synced:
            print(f"{color.BOLD + color.GREEN}- {color.BOLD + color.PURPLE}{s.name}{color.END}")
    except Exception as e:
        print(e)
    check_stop_file.start()


@bot.tree.command(name="isconnected", description="Check if you are connected")
async def isConnected_slash(interaction: discord.Interaction):
    connected: bool = True
    print("Called isconnected")
    await interaction.response.send_message(f"You are `{'connected' if connected else 'not connected'}`")

@bot.tree.command(name="disconnect", description="Disconnect the bot")
async def disconnect_slash(interaction: discord.Interaction):
    await interaction.response.send_message(f"Succesfully disconnected")
    await bot.close()
    sys.exit(0)


@bot.tree.command(name="takescreenshot", description="Take a screenshot of Minecraft")
async def takeScreenshot_slash(interaction: discord.Interaction):
    clearRequest()
    await interaction.response.send_message("Taking screenshot...")
    waiting = await interaction.original_response()
    current_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    msg = {
        "timestamp": current_time,
        "request": "screenshot"
    }
    with open("config/coresmodule/bot/sharedForMod.json", "w") as f:
        Json.dump(msg, f, indent=4)

    counter = 0

    import JsonCreator

    while True and counter <= 30:
        json = JsonCreator.getJson("sharedForBot")
        if json == None or json == {}:
            await asyncio.sleep(1)
            counter += 1
            continue
        else:
            try:
                path = json['answer']
                if path == "":
                    counter += 1
                    continue
                else:
                    f = discord.File(path)
                    await waiting.edit(content="Screenshot Taken:", attachments=[f])
                    await interaction.channel.send(file=f)
                    clearRequest(path)
                    break
            except Exception as e:
                print(e)
                counter += 1
                continue


@bot.tree.command(name="say", description="Say something in chat")
@app_commands.describe(message="The message you want to send")
async def say_slash(interaction: discord.Interaction, message: str):
    clearRequest()
    current_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    msg = {
        "timestamp": current_time,
        "request": "say",
        "content": message
    }
    with open("config/coresmodule/bot/sharedForMod.json", "w") as f:
        Json.dump(msg, f, indent=4)

    counter = 0

    import JsonCreator

    while True and counter <= 30:
        json = JsonCreator.getJson("sharedForBot")
        if json == None or json == {}:
            time.sleep(1)
            counter += 1
            continue
        else:
            try:
                if json['answer'] == "done":
                    await interaction.response.send_message(f"Message sent")
                    clearRequest()
                    break
                else:
                    counter += 1
                continue

            except Exception as e:
                print(e)
                counter += 1
                continue



@bot.tree.command(name="command", description="Run a command")
@app_commands.describe(command="The message you want to send")
async def command_slash(interaction: discord.Interaction, command: str):
    clearRequest()
    current_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    msg = {
        "timestamp": current_time,
        "request": "command",
        "content": command
    }
    with open("config/coresmodule/bot/sharedForMod.json", "w") as f:
        Json.dump(msg, f, indent=4)

    counter = 0

    import JsonCreator

    while True and counter <= 30:
        json = JsonCreator.getJson("sharedForBot")
        if json == None or json == {}:
            time.sleep(1)
            counter += 1
            continue
        else:
            try:
                if json['answer'] == "done":
                    await interaction.response.send_message(f"Command ran")
                    clearRequest()
                    break
                else:
                    counter += 1
                continue

            except Exception as e:
                print(e)
                counter += 1
                continue


def clearRequest(ScreenshotPath=None):
    with open("config/coresmodule/bot/sharedForBot.json", "w") as f:
        f.write("{}")

    if ScreenshotPath != None:
        if os.path.exists(ScreenshotPath):
            os.remove(ScreenshotPath)
            print("Deleted Screenshot at: " + ScreenshotPath)




@tasks.loop(seconds=2)
async def check_stop_file():
    stop_file_path = "config/coresmodule/bot/stop.txt"
    if os.path.exists(stop_file_path):
        try:
            with open(stop_file_path, "r") as f:
                content = f.read().strip().lower()
                if "true" in content:
                    print(f"{color.RED}Stop file detected. Shutting down bot...{color.END}")
                    await bot.close()
                    sys.exit(0)
        except Exception as e:
            print(f"Error reading stop.txt: {e}")


with open("config/coresmodule/bot/token.txt") as f:
    token = f.read().strip()
    bot.run(token)