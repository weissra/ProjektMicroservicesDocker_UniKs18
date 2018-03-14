package main

import (
	"flag"
	"fmt"
	"html/template"
	"log"
	"net/http"
	"io/ioutil"
	"encoding/json"
)

// esregistry Response format
type Response struct {
	Took     int  `json:"took"`
	TimedOut bool `json:"timed_out"`
	Shards struct {
		Total      int `json:"total"`
		Successful int `json:"successful"`
		Skipped    int `json:"skipped"`
		Failed     int `json:"failed"`
	} `json:"_shards"`
	TotalHits struct {
		Total    int         `json:"total"`
		MaxScore float64     `json:"max_score"`
		Hits     []SingleHit `json:"hits"`
	} `json:"hits"`
}
type SingleHit struct {
	Index string  `json:"_index"`
	Type  string  `json:"_type"`
	ID    string  `json:"_id"`
	Score float64 `json:"_score"`
	Source struct {
		Name        string `json:"name"`
		ServiceType string `json:"serviceType"`
		Format      string `json:"format"`
		ReturnType  string `json:"returnType"`
		URLString   string `json:"urlString"`
		Hostname    string `json:"hostname"`
		Speed       int    `json:"speed"`
		Port        int    `json:"port"`
	} `json:"_source"`
}

type page struct {
	Title      string
	SingleHits []SingleHit
}

// Command-line flags.
var (
	httpAddr   = flag.String("http", ":8080", "Listen address")
	test       = flag.Float64("testfloat", 1.66, "Test Float Value")
)


func main() {
	fmt.Println("main func was started")
	http.HandleFunc("/getMicroservices", getMicroservices)
	http.HandleFunc("/getAction", getAction)
	http.HandleFunc("/", serveHTTP)

	flag.Parse()
	//changeURL := fmt.Sprintf("%sgo%s", baseChangeURL, *version)
	log.Fatal(http.ListenAndServe(*httpAddr, nil))
}

// ServeHTTP implements the HTTP user interface.
func serveHTTP(w http.ResponseWriter, r *http.Request) {
	err := tmpl.Execute(w, nil)
	if err != nil {
		log.Print(err)
	}
}

func getMicroservices(w http.ResponseWriter, r *http.Request) {
	fmt.Println("Button was clicked!")
	fmt.Println("method:", r.Method) //get request method

	if r.Method == "GET" {
		// Make a get request
		rs, err := http.Get("http://esregistry:9200/_all/_search?q=_type:microservice&size=100")

		fmt.Println("Get sent")

		// Process Response
		if err != nil {
			panic(err) // More idiomatic way would be to print the error and die unless it's a serious error
		}
		defer rs.Body.Close()

		bodyBytes, err := ioutil.ReadAll(rs.Body)
		if err != nil {
			panic(err)
		}
		//read Response to string
		//fmt.Println(string(bodyBytes[:]))

		//parse json
		var jsonResp Response

		if err = json.Unmarshal(bodyBytes, &jsonResp); err != nil {
			fmt.Println("error json unmarshal", err)
		}

		//get single structs
		responseSingleHits = jsonResp.TotalHits.Hits

		for _, hit := range responseSingleHits {
			fmt.Println("ID: ", hit.ID)
		}

		templates := template.New("template")
		tt := templates.New("Body")
		tt, err = tt.Parse(doc)

		if err != nil {
			fmt.Println("error doc ", err)
		}

		tl := templates.New("List")
		tl, err = tl.Parse(docList)

		if err != nil {
			fmt.Println("error list", err)
		}

		page := page{Title: "Microservices list", SingleHits: responseSingleHits}
		templates.Lookup("Body").Execute(w, page)

	}
}

var responseSingleHits []SingleHit

func getAction(w http.ResponseWriter, r *http.Request) {
	idString := r.URL.Query().Get("request")

	for _, hit := range responseSingleHits {
		if idString == hit.ID {
			fmt.Println("hit struct: ", hit)

			rs, err := http.Get(hit.Source.URLString + "/info")

			// Process Response
			if err != nil {
				panic(err) // More idiomatic way would be to print the error and die unless it's a serious error
			}
			defer rs.Body.Close()

			bodyBytes, err := ioutil.ReadAll(rs.Body)
			if err != nil {
				panic(err)
			}
			//read Response to string
			jsonResponse := string(bodyBytes[:])
			fmt.Println(jsonResponse)
			fmt.Fprintf(w, "json: %s", jsonResponse)
		}
	}
}

// tmpl is the HTML template that drives the user interface.
var tmpl = template.Must(template.New("tmpl").Parse(`
<!DOCTYPE html>
<html>
	<head><title>Go webserver - microservices</title></head>
	<body>
		<center>
			<h1>HTML microservice display</h1>
			<form action="/getMicroservices" method="get">
    			<button type="submit">Get Request for Microservices hostname</button>
			</form>
		</center>
	</body>
</html>
`))

const docList = `
{{range .}}
	<tr>
		<td>
			<form action="/getAction" method="get">
    			<button value={{.ID}} name=request type="submit">GET</button>
			</form>
		</td>
		<td>{{.Source.ServiceType}}</td>
		<td>{{.ID}}</td>
		<td>{{.Source.Name}}</td>
		<td>{{.Source.Format}}</td>
		<td>{{.Source.ReturnType}}</td>
		<td>{{.Source.Speed}}</td>
		<td>{{.Source.URLString}}</td>
		<td>{{.Source.Hostname}}</td>
		<td>{{.Source.Port}}</td>
	</tr>
{{end}}

`

const doc = `
<!DOCTYPE html>
<html>
    <head><title>{{.Title}}</title></head>
    <body>
        <h1>Microservices registered in ES registry:</h1>
		<table>
			<th>Action</th>
 			<th>Type</th>
    		<th>ID</th>
			<th>Name</th>
			<th>Format</th>
			<th>ReturnType</th>
			<th>Speed</th>
			<th>URL</th>
			<th>Hostname</th>
			<th>Port</th>
   		    {{template "List" .SingleHits}}
		</table>
    </body>
</html>
`

var displaytmp = template.Must(template.New("displaytmp").Parse(`
<!DOCTYPE html>
<html>
	<head><title>Go webserver - microservices</title></head>
	<body>
		<center>
			<h1>HTML microservice display</h1>
			<form action="/getMicroservices" method="get">
    			<button type="submit">Get Request for Microservices hostname</button>
			</form>
			<form action="/getMicroservices2" method="get">
    			<button type="submit">Get Request for Microservices localhost</button>
			</form>
		</center>
	</body>
</html>
`))
