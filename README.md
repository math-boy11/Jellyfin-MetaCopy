# Jellyfin MetaCopy
A simple tool to copy metadata between Jellyfin items.

- [Jellyfin MetaCopy](#jellyfin-metacopy)
  - [Overview](#overview)
  - [Setup](#setup)
  - [Running](#running)
  - [Supported Item Types](#supported-item-types)
  - [Contributing](#contributing)
  - [Contact](#contact)

## Overview
This tool was created to help [Jellyfin](https://jellyfin.org/) users easily transfer metadata between items — especially useful when movies are mistakenly categorized as TV shows or episodes are misfiled as specials. Rather than manually re-entering metadata, this tool lets you copy it (including images) between any two items with a few simple steps!

## Setup
This tool requires a config file. Create a `config.properties` file and add your config options.

- serverUrl: The URL of your Jellyfin server. Make sure it does not have a slash at the end.
- apiKey: Create an API key in the Jellyfin admin dashboard (Dashboard -> API Keys)
- userId: The ID of any user that has access to both items
- sourceItemId: The ID of your source item
- targetItemId: The ID of your target item
- copyImages: Whether or not to copy the item images

Example:
```
serverUrl=<YOUR_JELLYFIN_SERVER_URL>
apiKey=<YOUR_JELLYFIN_API_KEY>
userId=95707b986fac4983b52aa8e7cdb5aafa
sourceItemId=0194df9f418205064cef85fa7359361e
targetItemId=4ca8a8288bbaf7b1370a7caec8c04828
copyImages=true
```

## Running
Download the latest JAR from [here](https://github.com/math-boy11/Jellyfin-MetaCopy/releases/latest/download/jellyfin-metacopy-1.0-SNAPSHOT-jar-with-dependencies.jar) and run the tool with the following command. Java 21 or later is required.
```
java -jar NAMEOFJARADDLATER.jar path/to/config.properties
```

If you're a developer, you can clone the repository and run it using [Maven](https://maven.apache.org/).

## Supported Item Types
This tool supports copying metadata between different item types (e.g., Episode to Movie, Movie to TV Show). The currently supported types are:

- Movie
- Series / TV Show
- Season
- Episode
- Video / Extras

If your item type isn't listed, feel free to [contribute to the project](#contributing)!

## Contributing
If you're a developer and would like to contribute by adding new features, supporting additional item types, or making improvements, feel free to open a GitHub issue or pull request. All contributions are welcome!

## Contact
For questions or suggestions, open a GitHub issue or email me at [theostechtipsinquires@gmail.com](mailto:theostechtipsinquires@gmail.com).
