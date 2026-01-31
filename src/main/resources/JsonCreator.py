import json
import os

# Get the absolute path of the directory where this script is located
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

def createJson(name, data=None, specified_folder=''):
    """Create a JSON file with the given name and data.
    If specified_folder is provided, it will create the file in that folder relative to BASE_DIR.
    If data is None, it will create an empty JSON object.
    """
    if specified_folder:
        # Construct the full path to the specified folder
        folder_path = os.path.join(BASE_DIR, specified_folder)
        os.makedirs(folder_path, exist_ok=True) # Ensure the directory exists
    else:
        folder_path = BASE_DIR
    
    file_path = os.path.join(folder_path, f'{name}.json')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        if data is None:
            data = {}
        json.dump(data, f, indent=4)

def addJson(name, data, specified_folder=''):
    """Add or update data in an existing JSON file.
    If specified_folder is provided, it will look for the file in that folder relative to BASE_DIR.
    """
    if specified_folder:
        folder_path = os.path.join(BASE_DIR, specified_folder)
    else:
        folder_path = BASE_DIR
        
    file_path = os.path.join(folder_path, f'{name}.json')

    try:
        with open(file_path, 'r+', encoding='utf-8') as f:
            existing_data = json.load(f)
            existing_data.update(data)
            f.seek(0)  # Rewind to the beginning of the file
            json.dump(existing_data, f, indent=4)
            f.truncate() # Remove remaining part if new data is smaller
    except FileNotFoundError:
        print(f"Error: File '{file_path}' not found. Cannot add data.")
    except json.JSONDecodeError:
        print(f"Error: Could not decode JSON from '{file_path}'. File might be corrupted or empty.")
    except Exception as e:
        print(f"An unexpected error occurred while adding data to '{file_path}': {e}")


def getJson(name, specified_folder=''):
    """Retrieve data from a JSON file.
    If specified_folder is provided, it will look for the file in that folder relative to BASE_DIR.
    Returns the JSON data or None if the file is not found or corrupted.
    """
    if specified_folder:
        folder_path = os.path.join(BASE_DIR, specified_folder)
    else:
        folder_path = BASE_DIR
        
    file_path = os.path.join(folder_path, f'{name}.json')

    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            return data
    except FileNotFoundError:
        print(f"Error: File '{file_path}' not found.")
        return None
    except json.JSONDecodeError:
        print(f"Error: Could not decode JSON from '{file_path}'. File might be corrupted or empty.")
        return None
    except Exception as e:
        print(f"An unexpected error occurred while getting data from '{file_path}': {e}")
        return None


def delJson(name, specified_folder=''):
    """Delete a JSON file.
    If specified_folder is provided, it will look for the file in that folder relative to BASE_DIR.
    If the folder becomes empty afterward, it will also be deleted.
    """
    if specified_folder:
        folder_path = os.path.join(BASE_DIR, specified_folder)
    else:
        folder_path = BASE_DIR
        
    file_path = os.path.join(folder_path, f'{name}.json')

    try:
        os.remove(file_path)

        # Check if the folder is empty and delete it if it is
        # Only attempt if a specified_folder was actually used
        if specified_folder:
            try:
                os.rmdir(folder_path)
            except OSError:
                # This error means the directory is not empty (e.g., contains other files/folders)
                print(f"Folder '{folder_path}' is not empty or could not be deleted.")
            except Exception as e:
                print(f"An unexpected error occurred while trying to delete folder '{folder_path}': {e}")

    except FileNotFoundError:
        print(f"Error: File '{file_path}' does not exist.")
    except Exception as e:
        print(f"An unexpected error occurred while deleting '{file_path}': {e}")
