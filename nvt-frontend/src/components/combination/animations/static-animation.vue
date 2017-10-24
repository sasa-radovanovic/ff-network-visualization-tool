<template lang="pug">

    div
        snackbar(:text="snackBarText", :show="snackBarShow")
        v-flex.mt-5(xs10, offset-xs1)
            h5 Static network visualization
            #mapCanvas.static-map-holder
                p Map holder



</template>


<script>

    import { mapConfigurationFactory, generatePin, generateInfoWindow } from './../../../services/map-config-service'
    import { getRotations } from './../../../services/rotation-service'
    import Snackbar from './../../../common/components/snackbar.vue'

    export default {
        components: {
            Snackbar
        },
        data () {
            return {
                snackBarText: '',
                snackBarShow: false,
                mapObject: null,
                rotations: [],
                rotationMarkers: [],
                airportsProcessed: {},
                color: ''
            }
        },
        created() {
            if (this.$route.params.combinationId === undefined) {
                this.$router.push({name:'combinations'})
            } else {
                this.color = this.$route.params.color
                getRotations(this.$route.params.combinationId).then(rsp => {
                    this.rotations = rsp
                    this.loadMap()
                }).catch(err => {
                    this.snackBarText = 'Error loading rotations for combination'
                    this.snackBarShow = !this.snackBarShow
                })
            }
        },
        mounted() {

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
                    zoom: 3,
                    center: new google.maps.LatLng(51.5072,0.1275),
                    styles: mapConfigurationFactory()
                };
                this.mapObject	=	new google.maps.Map(document.getElementById('mapCanvas'), options);
                this.loadRotationsToMap(this.rotations)
            },
            loadRotationsToMap(rotations) {
                let bounds = new google.maps.LatLngBounds();
                rotations.forEach(rot => {

                    if (this.airportsProcessed[rot.originIataCode] === undefined) {
                        var markerOrigin = new google.maps.Marker({
                            position: {
                                lat: rot.originLatitude,
                                lng: rot.originLongitude
                            },
                            map: this.mapObject,
                            icon: generatePin(this.color.substring(1)),
                            title: rot.originName
                        });
                        this.airportsProcessed[rot.originIataCode] = true
                        markerOrigin.setMap(this.mapObject)
                        markerOrigin.setMap(this.mapObject)

                        var _color = this.color

                        markerOrigin.addListener('click', function() {
                            generateInfoWindow(rot.originIataCode,
                                rot.originIcaoCode, rot.originName, rot.originCityName, rot.originCountry,
                                rot.originLongitude, rot.originLatitude, _color).open(this.mapObject, markerOrigin);
                        });
                    }



                    if (this.airportsProcessed[rot.destinationIataCode] === undefined) {

                        var markerDestination = new google.maps.Marker({
                            position: {
                                lat: rot.destinationLatitude,
                                lng: rot.destinationLongitude
                            },
                            map: this.mapObject,
                            icon: generatePin(this.color.substring(1)),
                            title: rot.destinationName
                        });

                        this.airportsProcessed[rot.destinationIataCode] = true
                        markerDestination.setMap(this.mapObject)

                        markerDestination.addListener('click', function() {
                            generateInfoWindow(rot.destinationIataCode,
                                rot.destinationIcaoCode, rot.destinationName, rot.destinationCityName, rot.destinationCountry,
                                rot.destinationLongitude, rot.destinationLatitude, _color).open(this.mapObject, markerDestination);
                        });
                    }

                    var flightPlanCoordinates = [
                        {
                            lat: rot.destinationLatitude,
                            lng: rot.destinationLongitude
                        },
                        {
                            lat: rot.originLatitude,
                            lng: rot.originLongitude
                        }
                    ]

                    var flightPath = new google.maps.Polyline({
                        path: flightPlanCoordinates,
                        geodesic: true,
                        strokeColor: this.color,
                        strokeOpacity: 0.5,
                        strokeWeight: 1
                    })

                    flightPath.setMap(this.mapObject)

                    bounds.extend(new google.maps.LatLng(rot.destinationLatitude, rot.destinationLongitude));
                    bounds.extend(new google.maps.LatLng(rot.originLatitude, rot.originLongitude));

                })

                this.mapObject.fitBounds(bounds);
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
        min-height: 600px;
        height: 600px;
    }
</style>