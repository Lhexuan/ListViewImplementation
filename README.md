ListViewImplementation
======================

Overview

This project is implementing ListView widget of android that displays the content from a json file and 
loads images from the urls that are specified in json file.

Specification

Create an Android app which:

Ingests a json feed from https://www.dropbox.com/s/g41ldl6t0afw9dv/facts.json
The gson library can be used to parse this json if desired.
The feed contains a title and a list of rows

Displays the content in a ListView
The title in the ActionBar should be updated from the json
Each row should be the right height to display its own content and no taller. 
No content should be clipped. This means some rows will be larger than others.

Loads the images lazily
Don't download them all at once, but only as needed
Refresh function
Either place a refresh button or use swipe to refresh.
Should not block UI when loading the data from the json feed.


Features:
1. Applied Holder pattern for better performance.
2. Used AsyncTask implementation that provide easy and robust way to deal with background task. Also for quickly loading the images executor is also implemented.
3. Lru memory cache has been used to provide seemless flow of activity and images can be draw quickly.
4. Smooth flow of GUI.
5. App also stores downloaded images in the internal memory managed by android. 
6. Error handling is provided so that user can see what is going on if images or page is not appear.

TestCases:

1. If an invalid url is passed that does not contain a file
Status: Pass

2. If an valid url is passed that does not contain a valid json file
Status: Pass

3. No Network connection either at startup of app or in between when images are loaded.
Status: pass

4. Changing the JSON file dynamically, Activity should refresh and display corrent content of json file.
Status: pass


