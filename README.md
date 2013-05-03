yit-android-rssreader
=====================

########################################################################
########## Yarl IT Hub - Android Assignment - RSS Reader ###############
########################################################################

1.GUI Layouts:
----------------
    1.1 Home Page  
    1.2 Add New RSS URL
    1.3 RSS Feed List
    1.4 Display Webpage

        1.1 Home Page:
        ----------------
        In this Layout displays, you are added RSS Feed websites.
        In the top right corner has PLUS button, If you click that button 
        you can go to another layout and add your favourite rss feed website.
        And It will display in the Listview with Title, and Link.
        
        1.2 Add New RSS URL
        ------------------
        In this layout display the input field for adding rss feed url
        
        for an example: www.adaderana.lk/rss.php
                        www.newsfirst.lk/english/rss.xml
                        
        1.3 RSS Feed List
        ------------------
        RSS Feed item displaying with Title, Publication Date Time and small Description in Listview.
        It will generate the data from you select the particular website in Home Page.
        
        1.4 Display Message
        -------------------
        It displays web content of particular news in webpage.

2. Features
--------------
    2.1 Add your Favourite RSS Feed Url
    2.2 Can read RSS News for any Newspaper, Channel and Entertainment site.
    2.3 Delete RSS Feed Website as you wish
          In home page, you can get the delete message, when you make a long press on selected website in listview.

3. Validation
--------------
    3.1 You have to Enter the proper rss url link, otherwise show the toast message with detail.
    3.2 You can't add the same rss link, it's already added.
    3.3 If network isn't available - Give a Alert Message and open your device network settings panel.

              
              
