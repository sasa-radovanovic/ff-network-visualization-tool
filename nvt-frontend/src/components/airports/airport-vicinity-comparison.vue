<template lang="pug">
    div
        v-container.mt-3.mb-5(grid-list-md)
            v-layout(row, wrap)
                v-flex.mt-5.mb-5(xs10, offset-xs1)
                    v-card
                        v-card-media(src="/static/card-header-5.png", height="200px")
                        v-card-title(primary-title)
                            h5 Comparison with airports in vicinity
                        v-card-text
                            p {{basicData.name}}, {{basicData.city}}, {{basicData.country}}

                            v-slider(track-color="blue", v-model="radius", thumb-label, step="50", snap, max="1000", min="100", label="Distance from airport in km")

                            v-btn(color="orange", dark, @click="loadComparison") Compare

                            v-divider.mt-2.mb-3
                            div
                                p Showing {{airports.length}} airports in {{radiusUsed}}km radius of around {{basicData.name}} airport
                                #mapCanvas.static-map-holder
                                    p

                                v-data-table.elevation-2.maxed-height(:headers='headers', :items='airports', hide-actions, v-if="airports.length > 0")
                                    template(slot='items', slot-scope='props')

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.index + 1}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.iataCode}} / {{props.item.icaoCode}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.name}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.city}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.country}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.carriers}}

                                        td(:class="{'this-airport' : props.item.iataCode === basicData.iataCode}") {{props.item.routes}}




</template>


<script>

    import { mapConfigurationFactory, generatePin, generateInfoWindow, otherAirportsColor, primaryAirportColor  } from './../../services/map-config-service'

    import { airportVicinityStats } from './../../services/airport-service'

    export default {
        data() {
            return {
                headers: [
                    { text: 'Index', align: 'left', sortable: false, value: 'index'
                    },
                    { text: 'Code', align: 'left', sortable: true, value: 'iataCode'
                    },
                    { text: 'Name', value: 'name', align: 'left', sortable: true },
                    { text: 'City', value: 'city', align: 'left', sortable: true  },
                    { text: 'Country', value: 'country', align: 'left', sortable: true  },
                    { text: 'No of carriers', value: 'carriers', align: 'left', sortable: true  },
                    { text: 'Routes', value: 'routes', align: 'left', sortable: true  }
                ],
                basicData: {},
                detailedData: {},
                radius: 100,
                radiusUsed: 0,
                comparisonLoaded: false,
                mapObject: null,
                circle: null,
                airports: [],
                markers: []
            }
        },
        created() {
            if (this.$route.params.basicData === undefined || this.$route.params.detailedData === undefined) {
                this.$router.push({'name': 'airport-data'})
            } else {
                this.basicData = this.$route.params.basicData
                this.detailedData = this.$route.params.detailedData
            }
        },
        mounted() {
            this.loadComparison()
        },
        methods: {
            loadMap() {
                var options = {
                    draggable: true,
                    panControl: true,
                    streetViewControl: false,
                    scrollwheel: true,
                    scaleControl: true,
                    disableDefaultUI: false,
                    disableDoubleClickZoom: false,
                    zoom: 4,
                    center: new google.maps.LatLng(this.detailedData.latitude,this.detailedData.longitude),
                    styles: mapConfigurationFactory()
                };
                this.mapObject	=	new google.maps.Map(document.getElementById('mapCanvas'), options);
                this.loadCircle()
            },
            loadComparison () {

                this.comparisonLoaded = true

                if (this.circle !== null) {
                    this.circle.setMap(null)
                    this.circle = null
                }

                this.markers.forEach(m => {
                    m.setMap(null)
                })

                this.markers = []

                this.loadMap()
                this.loadPrimaryAirport()
                this.loadAirports()
            },
            loadCircle() {
                this.circle = new google.maps.Circle({
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35,
                    map: this.mapObject,
                    center: new google.maps.LatLng(this.detailedData.latitude,this.detailedData.longitude),
                    radius: this.radius * 1000
                });
            },
            loadPrimaryAirport() {
                var marker = new google.maps.Marker({
                    position: {
                        lat: this.detailedData.latitude,
                        lng: this.detailedData.longitude
                    },
                    map: this.mapObject,
                    icon: generatePin(primaryAirportColor().substring(1)),
                    title: this.basicData.name
                });
                marker.setMap(this.mapObject)

                let self = this

                marker.addListener('click', function() {
                    generateInfoWindow(self.basicData.iataCode,
                        self.basicData.icaoCode, self.basicData.name, self.basicData.city, self.basicData.country,
                        self.detailedData.longitude, self.detailedData.latitude, primaryAirportColor()).open(self.mapObject, marker);
                });

                this.markers.push(marker)
            },
            loadAirports() {
                this.radiusUsed = this.radius

                airportVicinityStats(this.basicData.iataCode, this.radius).then(rsp => {
                    this.airports = rsp.airports
                    this.airports.forEach(airport => {

                        if (airport.iataCode !== this.basicData.iataCode) {

                            var marker = new google.maps.Marker({
                                position: {
                                    lat: airport.latitude,
                                    lng: airport.longitude
                                },
                                map: this.mapObject,
                                icon: generatePin(otherAirportsColor().substring(1)),
                                title: this.basicData.name
                            });
                            marker.setMap(this.mapObject)

                            let self = this

                            marker.addListener('click', function () {
                                generateInfoWindow(airport.iataCode,
                                    airport.icaoCode, airport.name, airport.city, airport.country,
                                    airport.longitude, airport.latitude, otherAirportsColor()).open(self.mapObject, marker);
                            });

                            this.markers.push(marker)
                        }
                    })
                })
            }
        }
    }
</script>


<style scoped>
    .map-holder {
        position: absolute;
        bottom: 0px;
        left: 0px;
        width: 100%;
        height: 100%;
        background: #fff;
        z-index: 0;
    }
    .static-map-holder {
        z-index: 0;
        background-color: darkgrey;
        min-height: 500px;
        height: 500px;
    }
    .this-airport {
        background-color: lightblue !important;
    }
</style>