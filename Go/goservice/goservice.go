package main

import (
	"net/http"
	"github.com/labstack/echo"
	"github.com/labstack/echo/middleware"
	"os"
	"fmt"
	"strconv"
	"regexp"
	"encoding/json"
	"bytes"
	"strings"
	"io/ioutil"
)

//this services information with json conversion binding info
type ServiceProperty struct {
	ID          string `json:"id,omitempty"`
	Name        string `json:"name"`
	ServiceType string `json:"serviceType"`
	Format      string `json:"Format"`
	ReturnType  string `json:"returnType"`
	URLString   string `json:"URLString"`
	Hostname    string `json:"hostname"`
	Speed       int    `json:"speed"`
	Port        int    `json:"port"`
}

//responses of the esregistry for registering and deserialization binding for json
type Response struct {
	Index   string `json:"_index"`
	Type    string `json:"_type"`
	ID      string `json:"_id"`
	Version int    `json:"_version"`
	Result  string `json:"result"`
	Shards struct {
		Total      int `json:"total"`
		Successful int `json:"successful"`
		Failed     int `json:"failed"`
	} `json:"_shards"`
	SeqNo       int `json:"_seq_no"`
	PrimaryTerm int `json:"_primary_term"`
}

var esregistryUrl string
var hostname string
var port int
var e *echo.Echo
var serviceProp ServiceProperty

func main() {
	fmt.Println("This service is started")

	//check launch args for validity and save them
	checkLaunchArgs(os.Args[1:])

	//print saved values
	fmt.Printf("hostname %v | port %d | esregistryUrl %v \n", hostname, port, esregistryUrl)

	//service property
	serviceProp = ServiceProperty{
		Name:        "goService",
		ServiceType: "goType",
		Hostname:    hostname,
		Port:        port,
		URLString:   "http://" + hostname + ":" + strconv.Itoa(port),
		Format:      "empty",
		Speed:       2000,
		ReturnType:  "empty"}

	// echo instance
	e = echo.New()

	// middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())

	// routes
	e.GET("/info", info)
	e.GET("/id", id)

	//TODO check for resregistry availabilty/online

	//register service
	registerOnEsRegistry()

	// Start server
	e.Logger.Fatal(e.Start(":" + strconv.Itoa(port)))
}

//register this service on
func registerOnEsRegistry() {
	fmt.Println("GoService trying to register on esregistry")

	//convert serviceProp to json
	jsonServiceProp, err := json.Marshal(&serviceProp)

	// post this services info to esregistry
	resp, err := http.Post("http://"+esregistryUrl+"/microservices/microservice/", "application/json", bytes.NewReader(jsonServiceProp))
	if err != nil {
		fmt.Println("failed to post", err)
	}
	fmt.Println("resp", resp)

	// process response
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()

	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		panic(err)
	}
	//read response to string
	fmt.Println(string(bodyBytes[:]))

	//parse json response
	var response Response
	json.Unmarshal(bodyBytes, &response)

	if response.Result == "created" {
		serviceProp.ID = response.ID
		fmt.Printf("Received ID: %v -> Successfully registered microservice on ElasticSearch | %v \n", serviceProp.ID, serviceProp.Name)
	}
}

//Checks launch arguments and saves them in global vars
func checkLaunchArgs(launchArgs []string) {
	if len(launchArgs) != 3 {
		fmt.Println("Wrong launch arguments. [port] [hostname] [esregistry url] are required")
		os.Exit(3)
	}
	esregistryUrl = launchArgs[2]
	hostname = launchArgs[1]
	portTemp := launchArgs[0]

	rHostname, _ := regexp.Compile("^[a-zA-Z0-9]+$")
	rPort, _ := regexp.Compile("^\\d{4,5}$")
	rRegistryUrl, _ := regexp.Compile("^[a-zA-Z0-9]+:\\d{4,5}$")

	if !rHostname.MatchString(hostname) || !rPort.MatchString(portTemp) || !rRegistryUrl.MatchString(esregistryUrl) {
		fmt.Println("Wrong launch arguments. [port] [hostname] [esregistry url] are required")
		os.Exit(3)
	}

	portNumber, err := strconv.Atoi(portTemp)
	if err != nil {
		fmt.Println("Lauch argument for [port] is not a number")
		os.Exit(3)
	} else {
		port = portNumber
	}

	//check if started inside docker
	if hostname == "localhost" {
		indexDouble := strings.LastIndex(esregistryUrl, ":")
		substring := esregistryUrl[indexDouble:]
		esregistryUrl = hostname + substring
	}
}

// TODO /info handler
func info(c echo.Context) error {
	return c.String(http.StatusOK, "Hello, World!")
}

// id handler
func id(c echo.Context) error {
	return c.JSON(http.StatusOK, serviceProp)
}
