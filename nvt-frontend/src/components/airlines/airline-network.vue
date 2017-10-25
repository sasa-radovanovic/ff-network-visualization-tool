<template lang="pug">

    div
        v-container.mt-3.mb-5(grid-list-md)
            v-layout(row, wrap)
                v-flex.mb-3(xs12)
                    v-layout(row)
                        h5.mt-2 Airline network
                        v-spacer
                        v-btn(color="orange", dark)
                            v-icon(dark, left) flight
                            | Add airline
                        v-btn(color="orange", dark)
                            v-icon(dark, left) add_location
                            | Add combination

                        add-airline-dialog.mt-2(label="Add airline", action="Select", @selected-airline="onAirlineAdded")

                v-flex.mt-2(xs3, v-for="airline in airlinesInQueue", :key="airline.iata")
                    v-card
                        v-card-media(:src="getAirlineLogoUrl(airline.iata)", :style="{'background-color' : 'white', 'border' : '4px solid', 'border-color' : airline.color}", height="80px")
                        v-card-title(primary-title)
                            p {{airline.name}} ({{airline.iata}}), {{airline.country}}
                        v-card-actions
                            v-btn(flat, :style="{'color': airline.color}") Change color
                            v-btn(flat, :style="{'color': airline.color}") Remove
                v-flex(xs12)
                    v-checkbox(label="Codeshares", v-model="codeshare")
                v-flex.mt-1(xs12)
                    #mapCanvas.static-map-holder
                        p Map holder

</template>


<script>

    import { getAirlineRoutes } from './../../services/airline-service'
    import { mapConfigurationFactory, generateInfoWindow, generatePin, generateRoutePolyline } from './../../services/map-config-service'
    import AddAirlineDialog from './add-airline-dialog'

    export default {
        components: {
            AddAirlineDialog
        },
        data () {
            return {
                airline: {
                    uniqueId: '',
                    name: '',
                    iata: '',
                    icao: '',
                    country: '',
                    color: ''
                },
                airlinesInQueue: [],
                border: 'orange',
                airlineRoutes: {},
                codeshare: true,
                mapObject: null,
                routesOnMap: {},
                airportsOnMap: {},
                bounds: null,
                airportsProcessed: {},
                rotationsProcessed: {}
            }
        },
        created() {
            if (this.$route.params.airline === undefined) {
                this.$router.push({name: 'airlines'})
            } else {
                this.airline.uniqueId = this.$route.params.airline.uniqueId
                this.airline.name = this.$route.params.airline.name
                this.airline.iata = this.$route.params.airline.iataCode
                this.airline.icao = this.$route.params.airline.icaoCode
                this.airline.country = this.$route.params.airline.country
                this.airline.color = this.getRandomColor()
                this.airlinesInQueue.push(this.airline)
                this.bounds = new google.maps.LatLngBounds();
            }
        },
        mounted(){
            this.loadMap()
        },
        methods: {
            onAirlineAdded(item) {
                console.log('Added AIRLINE ', item)
            },
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
                this.retrieveAirlineRoutes(this.airline.uniqueId, this.airline.iata, this.airline.color)
            },
            retrieveAirlineRoutes(uniqueId, iata, color) {
                getAirlineRoutes(uniqueId, this.codeshare).then(rsp => {
                    console.log('ROUTES RETRIEVED ' + rsp.length)
                    this.airlineRoutes[iata] = rsp
                    this.prepareRoutes(rsp, iata, color)
                    this.drawMap()
                }).catch(err => {

                })
            },
            getAirlineLogoUrl(iata) {
                return 'https://daisycon.io/images/airline/?width=250&height=80&color=ffffff&iata=' + iata
            },
            getRandomColor() {
                var letters = '0123456789ABCDEF';
                var color = '#';
                for (var i = 0; i < 6; i++) {
                    color += letters[Math.floor(Math.random() * 16)];
                }
                return color;
            },
            prepareRoutes(newRoutes, airline, color) {

                console.log('PREPARE ROUTES PLEASEEEEE ' + newRoutes.length)


                newRoutes.forEach(r => {

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
                            this.airportsOnMap[r.originIataCode].color = '#737373'
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
                            this.airportsOnMap[r.destinationIataCode].color = '#737373'
                        }
                    }

                    if (this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode] === undefined) {
                        if (this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode] === undefined) {
                            this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode] = {}
                            this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline] = r
                            this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].color = color
                            this.routesOnMap[r.originIataCode + '/' + r.destinationIataCode][airline].return = false
                        } else {
                            this.routesOnMap[r.destinationIataCode + '/' + r.originIataCode][airline].return = true
                        }
                    }
                })
            },
            drawMap() {


                console.log('process')
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
                    }

                    this.bounds.extend(new google.maps.LatLng(a.data.latitude, a.data.longitude));
                })

                this.mapObject.fitBounds(this.bounds)

                let routes = Object.keys(this.routesOnMap)

                routes.forEach(key => {
                    let airlinesInRoute = Object.keys(this.routesOnMap[key])
                    airlinesInRoute.forEach(air => {
                        let route = this.routesOnMap[key][air]

                        //console.log(route)
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

                        flightPath.setMap(this.mapObject, flightPath)






                    })

                })

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