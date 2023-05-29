# weather-app
Get weather data based on lat/long of incoming request's ip address.

# Pre-requisite
- Java - 17
- Maven - 3.x.x
- Spring Boot - 3.1.0
- Mongo DB

# Steps to Set Up

- Clone this project

  git clone https://github.com/ShalakaP02/weather-app.git
  
- Set up Mongo DB as Docker Container

  -> _docker pull mongo:latest_     
  -> _docker run -d -p 27017:27017 -name weather mongo:latest_   
  
- Set your api key in application.properties for third party API : https://api.openweathermap.org    
    ip.weather.api-key= <your-api-key>   
  
- Build and run the app as Spring Boot Application : WeatherAppApplication.java   
  
  
# API Details   
 - Server Port : 8081 
 - Swagger Doc : http://localhost:8081/swagger-ui/index.html
 - API :  
    -- http://localhost:8081/weather         
    -- http://localhost:8081/weather/103.208.69.70     
    -- http://localhost:8081/weather/lat/18.6161/lon/73.7286    
  
