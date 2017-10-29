<template lang="pug">
    div
        snackbar(:text="snackBarText", :show="snackBarShow")
        v-container.mt-3.mb-5(grid-list-md)
            v-layout(row, wrap)
                v-flex.mt-5(xs10, offset-xs1)
                    v-card.mb-5
                        v-card-media(src="/static/card-header-5.png", height="200px")
                        v-card-title(primary-title)
                            v-layout(row, wrap)
                                v-flex(xs12)
                                    h5 Airport data
                                        airport-select-dialog.airport-selection(label="Select airport", action="Select", @return-action="onAirportSelected")
                                    p.ml-1(v-if="basicData.name !== undefined") {{basicData.name}} [{{basicData.iataCode}}], {{basicData.city}}, {{basicData.country}}
                        v-card-text(v-if="basicData.iataCode !== undefined")
                            #mapCanvas.static-map-holder
                                p

                            p(v-if="detailedData.operatingCarriers !== undefined") Operating carriers from {{basicData.iataCode}}: {{detailedData.operatingCarriers.length}}
                            operating-carriers(v-if="detailedData.operatingCarriers !== undefined", :carriers="detailedData.operatingCarriers")


                        v-card-actions.mb-5(v-if="basicData.iataCode !== undefined")
                            v-spacer
                            airport-select-dialog.airport-selection(label="Compare with other airport", action="Select", @return-action="compareAirportSelected")
                            v-btn(color="orange", dark) Compare with airports in vicinity





</template>


<script>

    import { mapConfigurationFactory, generatePin, generateInfoWindow } from './../../services/map-config-service'
    import AirportSelectDialog from './../combination/airports/airports-select-dialog'
    import Snackbar from './../../common/components/snackbar.vue'
    import { airportDetails } from './../../services/airport-service'
    import OperatingCarriers from './operating-carriers'

    export default {
        components: {
            Snackbar,
            AirportSelectDialog,
            OperatingCarriers
        },
        data () {
            return {
                snackBarText: '',
                snackBarShow: '',
                basicData: {},
                detailedData: {},
                mapObject: null,
                airportsProcessed: {},
                polylines: [],
                markers: [],
                comparisonBasicData: {},
                showComparison: 0
            }
        },
        methods: {
            compareAirportSelected(item) {
                this.comparisonBasicData = item
                this.$router.push({name: 'airport-comparison', params: {basicData1: this.basicData, detailedData1: this.detailedData, basicData2: item}})
            },
            loadMap() {
                var options = {
                    draggable: true,
                    panControl: true,
                    streetViewControl: false,
                    scrollwheel: true,
                    scaleControl: true,
                    disableDefaultUI: false,
                    disableDoubleClickZoom: false,
                    styles: mapConfigurationFactory()
                };
                this.mapObject	=	new google.maps.Map(document.getElementById('mapCanvas'), options);
                this.loadRotationsToMap(this.detailedData.connections)
            },
            loadRotationsToMap(rotations) {
                let bounds = new google.maps.LatLngBounds();

                bounds.extend(new google.maps.LatLng(this.detailedData.latitude, this.detailedData.longitude));

                var markerAirport = new google.maps.Marker({
                    position: {
                        lat: this.detailedData.latitude,
                        lng: this.detailedData.longitude
                    },
                    map: this.mapObject,
                    icon: generatePin("e69500"),
                    title: this.detailedData.name
                });

                markerAirport.setMap(this.mapObject)

                var _color = "#e69500"

                var self = this

                markerAirport.addListener('click', function() {
                    generateInfoWindow(self.basicData.iataCode,
                        self.basicData.icaoCode, self.basicData.name, self.basicData.city, self.basicData.country,
                        self.detailedData.longitude, self.detailedData.latitude, _color).open(self.mapObject, markerAirport);
                });

                this.markers.push(markerAirport)
                this.airportsProcessed[this.basicData.iataCode] = true

                rotations.forEach(rot => {



                    if (this.airportsProcessed[rot.iataCode] === undefined) {

                        var markerDestination = new google.maps.Marker({
                            position: {
                                lat: rot.latitude,
                                lng: rot.longitude
                            },
                            map: this.mapObject,
                            icon: generatePin("e69500"),
                            title: rot.name
                        });

                        markerDestination.setMap(this.mapObject)

                        var _color = "#e69500"


                        markerDestination.addListener('click', function() {
                            generateInfoWindow(rot.iataCode,
                                rot.icaoCode, rot.name, rot.city, rot.country,
                                rot.longitude, rot.latitude, _color).open(this.mapObject, markerDestination);
                        });

                        this.airportsProcessed[rot.iataCode] = true
                        this.markers.push(markerDestination)
                    }

                    var flightPlanCoordinates = [
                        {
                            lat: this.detailedData.latitude,
                            lng: this.detailedData.longitude
                        },
                        {
                            lat: rot.latitude,
                            lng: rot.longitude
                        }
                    ]

                    var flightPath = new google.maps.Polyline({
                        path: flightPlanCoordinates,
                        geodesic: true,
                        strokeColor: '#e69500',
                        strokeOpacity: 0.8,
                        strokeWeight: 1
                    })


                    flightPath.setMap(this.mapObject)

                    this.polylines.push(flightPath)

                    bounds.extend(new google.maps.LatLng(rot.latitude, rot.longitude));

                })

                //this.mapObject.fitBounds(bounds);
                let airportLocation = new google.maps.LatLng(this.detailedData.latitude, this.detailedData.longitude);
                this.mapObject.setZoom(6)
                this.mapObject.setCenter(airportLocation)
            },
            onAirportSelected(item) {

                this.markers.forEach(m => {
                    m.setMap(null)
                })

                this.polylines.forEach(p => {
                    p.setMap(null)
                })

                this.airportsProcessed = {}

                this.basicData = item
                airportDetails(item.iataCode).then(rsp => {
                    this.detailedData = rsp
                    if (this.mapObject === null || this.mapObject === undefined) {
                        this.loadMap()
                    } else {
                        this.loadRotationsToMap(this.detailedData.connections)
                    }
                })
            }
        }
    }

</script>


<style scoped>
    .airport-selection {
        float: right;
    }
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
        min-height: 300px;
        height: 300px;
    }
</style>