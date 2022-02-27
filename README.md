# SD-ucBusca
This project aims to create a search engine for web pages. Have a subset of functionality similar to Google.com, Bing.com and DuckDuckGo.com, including automatic indexing (Web crawler) and search (search engine). 
The System should have all relevant information about the pages, such as URL, title, a quote from the text and others that you consider important. When performing a search, a user gets the list of pages containing the searched words, with the required information, and the approximate number of search results. Only administrators can introduce specific URLs to be indexed by the system. Starting from these URLs, the system must iteratively or recursively index all the links found on each page indexed.


Objetives:
- Development of a web interface for the ucBusca application.
- Integration of a web interface with the developed application
- Use of Struts2, JavaServer Pages and JavaBeans.
- Follow the MVC architecture for web development.
- WebSockets application to communicate asynchronously with clients.
- Integrate the application with external REST services, using OAuth.


![alt text](https://camo.githubusercontent.com/7b8cce3f1748208cffb7f51914aeea021770644187113568ee27cd8ecc6e7d24/68747470733a2f2f692e6962622e636f2f4a79703933397a2f53637265656e73686f742d323032302d30392d31332d61742d31372d31352d31312e706e67)

Topics aborded:
- Server synchronization and effort division
- Websockets (for client notifications)
- HTTPS
- REST API (using Yandex translations)
- Oauth3 (logging in with Facebook)
- MVC model
- Authorization
- Authentication
- Exception handling
- Failover handling (eg. when the primary RMI server fails, the backup RMI server takes action and becomes primary)
- Web-crawling (using jsoup)
