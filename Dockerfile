FROM eclipse-temurin:17
LABEL authors="dfx81"
WORKDIR /home/netgame
EXPOSE 8080
EXPOSE 8000
COPY netgame.jar /home/netgame/netgame.jar
CMD ["java", "-jar", "netgame.jar", "8080", "8000"]