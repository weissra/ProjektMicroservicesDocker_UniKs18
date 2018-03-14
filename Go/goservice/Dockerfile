FROM golang:onbuild

WORKDIR /go/src/goService

# Golang package
RUN go get -u \
    github.com/labstack/echo/...
	
COPY . .
RUN dep ensure
	
	
EXPOSE 8020-8030
