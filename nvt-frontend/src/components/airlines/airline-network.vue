<template lang="pug">

    div
        v-container.mt-3.mb-5(grid-list-md)
            v-layout(row, wrap)
                v-flex.mb-3(xs12)
                    v-layout(row)
                        h5.mt-3 Airline network
                        v-spacer
                        add-combination-dialog.mt-2(label="Add combination", action="Select", @selected-combination="onCombinationAdded", :disable-modal="sumAirlinesCombinations === 4")
                        add-airline-dialog.mt-2(label="Add airline", action="Select", @selected-airline="onAirlineAdded", :disable-modal="sumAirlinesCombinations === 4")

                v-flex.mt-2(xs3, v-for="airline in airlinesInQueue", :key="airline.iata")
                    v-card
                        v-card-media(:src="getAirlineLogoUrl(airline.iata)", :style="{'background-color' : 'white', 'border' : '4px solid', 'border-color' : airline.color}", height="80px")
                        v-card-title(primary-title)
                            p {{airline.name}} ({{airline.iata}}), {{airline.country}}
                        v-card-actions
                            change-color-dialog(:current="airline.color", :name="airline.name", :iata="airline.iata", @selected-color="changeAirlineColor")
                            v-btn(flat, :style="{'color': airline.color}", @click="removeAirline(airline.iata)", :disabled = "sumAirlines == 1") Remove
                v-flex.mt-2(xs3, v-for="comb in combinationsInQueue", :key="comb.name")
                    v-card
                        v-card-media(:style="{'background-color' : comb.color}", height="80px")
                        v-card-title(primary-title)
                            p {{comb.name}}
                        v-card-actions
                            v-btn(flat, :style="{'color': comb.color}", @click="onCombinationRemoved(comb.id)") Remove
                v-flex(xs12)
                    v-checkbox(label="Codeshares", v-model="codeshare")
                v-flex.mt-1(xs12)
                    #mapCanvas.static-map-holder
                        p Map holder

</template>


<script>

    import {getAirlineRoutes} from './../../services/airline-service'
    import {
        mapConfigurationFactory,
        generateInfoWindow,
        generatePin,
        generateRoutePolyline
    } from './../../services/map-config-service'
    import AddAirlineDialog from './add-airline-dialog'
    import AddCombinationDialog from './../combination/add-combination-dialog'
    import ChangeColorDialog from './change-color-modal'
    import { getRotations } from './../../services/rotation-service'


    export default {
        components: {
            AddAirlineDialog,
            AddCombinationDialog,
            ChangeColorDialog
        },
        data () {
            return {
                // Primary airline data holder
                airline: {
                    uniqueId: '',
                    name: '',
                    iata: '',
                    icao: '',
                    country: '',
                    color: ''
                },
                // All airlines selected
                airlinesInQueue: [],
                // All combinations selected
                combinationsInQueue: [],

                // Data used for map processing
                airlineRoutes: {},
                codeshare: true,
                mapObject: null,
                routesOnMap: {},
                airportsOnMap: {},
                bounds: null,
                airportsProcessed: {},
                rotationsProcessed: {},

                markers: [],
                polylines: []
            }
        },
        created() {
            // If there is no data passed to component return to airlines page
            if (this.$route.params.airline === undefined) {
                this.$router.push({name: 'airlines'})
            } else {
                // Otherwise map objects and initiate
                this.airline.uniqueId = this.$route.params.airline.uniqueId
                this.airline.name = this.$route.params.airline.name
                this.airline.iata = this.$route.params.airline.iataCode
                this.airline.icao = this.$route.params.airline.icaoCode
                this.airline.country = this.$route.params.airline.country
                this.airline.color = this.getRandomColor()
                this.airlinesInQueue.push(this.airline)
            }
        },
        mounted(){
            // Once component is mounted load the map
            // NOTE: you cannot load the map on created lifecycle hook due to rendering process
            // GMaps API required div to be presented in the dom
            this.loadMap()
        },
        watch: {
          // Watch codeshare value change
          // If codeshare value changes, reload data
          codeshare(val) {
              console.log('change occured to ' + val)
              this.airportsOnMap = {}
              this.routesOnMap = {}

              this.airlinesInQueue.forEach(airline => {
                  this.retrieveAirlineRoutes(airline.uniqueId, airline.iata, airline.color, true)
              })
          }
        },
        methods: {

            // Change visual appearance of airline
            changeAirlineColor(data) {

                // Retrieve airline which is gonna be changed
                this.airlinesInQueue.filter(airline => {
                    if (airline.iata === data.iata &&
                            airline.name === data.name) {

                        // Apply new color
                        airline.color = data.color

                        // Reinitialize map and data on it
                        this.airportsOnMap = {}
                        this.routesOnMap = {}

                        this.combinationsInQueue.forEach(comb => {
                            this.prepareCombinationData(comb.id, comb.name, comb.color)
                        })

                        this.airlinesInQueue.forEach(airline => {
                            this.retrieveAirlineRoutes(airline.uniqueId, airline.iata, airline.color, true)
                        })
                    }
                })
            },
            // Remove certain airline from the queue
            removeAirline(iata) {
                this.airlinesInQueue = this.airlinesInQueue.filter(a => {
                    return a.iata !== iata
                })
                this.airportsOnMap = {}
                this.routesOnMap = {}

                this.combinationsInQueue.forEach(comb => {
                    this.prepareCombinationData(comb.id, comb.name, comb.color)
                })

                this.airlinesInQueue.forEach(airline => {
                    this.retrieveAirlineRoutes(airline.uniqueId, airline.iata, airline.color, true)
                })
            },
            // Add combination to the queue
            prepareCombinationData(id, name, color) {
                getRotations(id).then(rsp => {
                    this.prepareRoutes(rsp, name, color, true)
                    this.drawMap()
                })
            },
            // Trigger adding combination routine
            onCombinationAdded(item) {
                this.combinationsInQueue.push(item)
                this.prepareCombinationData(item.id, item.name, item.color)
            },
            // Trigger removing combination
            onCombinationRemoved(id) {

                this.airportsOnMap = {}
                this.routesOnMap = {}

                this.combinationsInQueue = this.combinationsInQueue.filter(c => {
                    return c.id !== id
                })

                this.combinationsInQueue.forEach(comb => {
                    this.prepareCombinationData(comb.id, comb.name, comb.color)
                })

                this.airlinesInQueue.forEach(airline => {
                    this.retrieveAirlineRoutes(airline.uniqueId, airline.iata, airline.color, true)
                })

            },
            // Add new airline to the list
            onAirlineAdded(item) {
                let newAirline = {}
                newAirline.uniqueId = item.uniqueId
                newAirline.name = item.name
                newAirline.iata = item.iataCode
                newAirline.icao = item.icaoCode
                newAirline.country = item.country
                newAirline.color = this.getRandomColor()
                this.airlinesInQueue.push(newAirline)
                this.retrieveAirlineRoutes(newAirline.uniqueId, newAirline.iata, newAirline.color, true)
            },
            // Load map and initialize data
            loadMap(){
                var options = {
                    draggable: true,
                    panControl: true,
                    streetViewControl: false,
                    scrollwheel: true,
                    scaleControl: true,
                    disableDefaultUI: false,
                    disableDoubleClickZoom: false,
                    zoom: 3,
                    center: new google.maps.LatLng(51.5072, 0.1275),
                    styles: mapConfigurationFactory()
                };
                this.mapObject = new google.maps.Map(document.getElementById('mapCanvas'), options);
                this.retrieveAirlineRoutes(this.airline.uniqueId, this.airline.iata, this.airline.color, true)
            },
            // Retrive routes of a certain airline
            retrieveAirlineRoutes(uniqueId, iata, color, initiateRedraw) {
                getAirlineRoutes(uniqueId, this.codeshare).then(rsp => {
                    this.prepareRoutes(rsp, iata, color, false)
                    if (initiateRedraw) {
                        this.drawMap()
                    }
                }).catch(err => {

                })
            },
            // Retrieve airline logo
            getAirlineLogoUrl(iata) {
                return 'https://daisycon.io/images/airline/?width=250&height=80&color=ffffff&iata=' + iata
            },
            // Form random color
            getRandomColor() {
                var letters = '0123456789ABCDEF';
                var color = '#';
                for (var i = 0; i < 6; i++) {
                    color += letters[Math.floor(Math.random() * 16)];
                }
                return color;
            },
            // Prepare objects before placing them on the map
            prepareRoutes(newRoutes, airline, color, isCombination) {


                newRoutes.forEach(r => {

                    // If airport/origin is not placed in the data yet
                    if (this.airportsOnMap[r.originIataCode] === undefined) {
                        this.airportsOnMap[r.originIataCode] = {}
                        this.airportsOnMap[r.originIataCode].color = color
                        this.airportsOnMap[r.originIataCode].data = {
                            iata: r.originIataCode,
                            icao: r.originIcaoCode,
                            name: r.originName,
                            city: r.originCityName,
                            country: r.originCountry,
                            longitude: r.originLongitude,
                            latitude: r.originLatitude
                        }
                    } else {
                        if (this.airportsOnMap[r.originIataCode].color !== color) {
                            this.airportsOnMap[r.originIataCode].color = '#808080'
                        }
                    }


                    if (this.airportsOnMap[r.destinationIataCode] === undefined) {
                        this.airportsOnMap[r.destinationIataCode] = {}
                        this.airportsOnMap[r.destinationIataCode].color = color
                        this.airportsOnMap[r.destinationIataCode].data = {
                            iata: r.destinationIataCode,
                            icao: r.destinationIcaoCode,
                            name: r.destinationName,
                            city: r.destinationCityName,
                            country: r.destinationCountry,
                            longitude: r.destinationLongitude,
                            latitude: r.destinationLatitude
                        }
                    } else {
                        if (this.airportsOnMap[r.destinationIataCode].color !== color) {
                            this.airportsOnMap[r.destinationIataCode].color = '#808080'
                        }
                    }

                    try {

                        if (this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode] === undefined) {
                            if (this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode] === undefined) {

                                this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode] = {}
                                this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline] = r
                                this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].color = color
                                this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].return = false
                                if (isCombination) {
                                    this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].codeshare = false
                                }
                            } else {
                                this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode][airline].return = true

                            }

                        } else {
                            if (this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline] === undefined) {

                                if (this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode] !== undefined) {
                                    if (this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode][airline] === undefined) {
                                        this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline] = r
                                        this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].color = color
                                        this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].return = false
                                        if (isCombination) {
                                            this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].codeshare = false
                                        }
                                    } else {
                                        this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode][airline].return = true
                                    }
                                } else {
                                    this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline] = r
                                    this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].color = color
                                    this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].return = false
                                    if (isCombination) {
                                        this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].codeshare = false
                                    }
                                }


                            }
                        }


                    } catch (e) {
                        console.error(e)
                    }
                })

            },
            // Execute drawing on the map
            drawMap() {

                this.airportsProcessed = {}

                this.bounds = new google.maps.LatLngBounds();

                this.markers.forEach(m => {
                    m.setMap(null)
                })

                this.polylines.forEach(p => {
                    p.setMap(null)
                })

                this.markers = []
                this.polylines = []

                let airports = Object.keys(this.airportsOnMap)
                airports.forEach(key => {
                    let a = this.airportsOnMap[key]
                    if (this.airportsProcessed[a.data.iata] === undefined) {
                        var airportMarker = new google.maps.Marker({
                            position: {
                                lat: a.data.latitude,
                                lng: a.data.longitude
                            },
                            map: this.mapObject,
                            icon: generatePin(a.color.substring(1)),
                            title: a.data.name
                        });
                        this.airportsProcessed[a.data.iata] = true
                        airportMarker.setMap(this.mapObject)

                        airportMarker.addListener('click', function () {
                            generateInfoWindow(a.data.iata,
                                a.data.icao, a.data.name, a.data.city, a.data.country,
                                a.data.longitude, a.data.latitude, a.color).open(this.mapObject, airportMarker);
                        });

                        this.markers.push(airportMarker)
                    }

                    this.bounds.extend(new google.maps.LatLng(a.data.latitude, a.data.longitude));
                })

                this.mapObject.fitBounds(this.bounds)

                let routes = Object.keys(this.routesOnMap)

                routes.forEach(key => {
                    let airlinesInRoute = Object.keys(this.routesOnMap[key])
                    airlinesInRoute.forEach(air => {
                        let route = this.routesOnMap[key][air]

                        var flightPlanCoordinates = [
                            {
                                lat: route.destinationLatitude,
                                lng: route.destinationLongitude
                            },
                            {
                                lat: route.originLatitude,
                                lng: route.originLongitude
                            }
                        ]

                        var lineSymbol = {
                            path: 'M 0,-1 0,1',
                            strokeOpacity: 1,
                            strokeWeight: 2,
                            scale: 3
                        }

                        let flightPath

                        console.log(route)

                        if (route.codeshare === false) {
                            flightPath = new google.maps.Polyline({
                                path: flightPlanCoordinates,
                                geodesic: true,
                                strokeColor: route.color,
                                strokeOpacity: 0.5,
                                strokeWeight: 2
                            })
                        } else {
                            flightPath = new google.maps.Polyline({
                                path: flightPlanCoordinates,
                                geodesic: true,
                                strokeColor: route.color,
                                strokeOpacity: 0.5,
                                strokeWeight: 0,
                                icons: [{
                                    icon: lineSymbol,
                                    offset: '50%',
                                    repeat: '15px'
                                }]
                            })
                        }

                        this.polylines.push(flightPath)

                        flightPath.setMap(this.mapObject)
                    })

                })

            }
        },
        computed: {
            sumAirlinesCombinations() {
                return this.airlinesInQueue.length + this.combinationsInQueue.length
            },
            sumAirlines () {
                return this.airlinesInQueue.length
            }
        }
    }
</script>


<style scoped>
    .static-map-holder {
        z-index: 0;
        background-color: darkgrey;
        min-height: 600px;
        height: 600px;
    }
</style>