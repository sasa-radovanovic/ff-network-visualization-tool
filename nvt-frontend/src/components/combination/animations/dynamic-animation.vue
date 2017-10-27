<template lang="pug">
    div
        snackbar(:text="snackBarText", :show="snackBarShow")
        v-flex.mt-5(xs10, offset-xs1)
            h5 Dynamic network visualization

            v-card
                v-card-title Settings
                v-card-text
                    v-flex(xs12)
                        v-slider(track-color="color", v-model="speedRatio", :disabled="animationStarted", thumb-label, step="2", snap, max="480", min="1", label="Animation speed")
                v-card-actions
                    v-spacer
                    v-btn(color="orange", dark, @click="animate", v-if="!animationStarted") Start animation
                    v-btn(color="orange", dark, @click="stop", v-if="animationStarted") Stop animation
                    v-btn(color="orange", dark, @click="pause", v-if="animationStarted") Pause animation

            v-card
                v-card-text
                    weekly-countdown(:day="day", :hours="hours", :minutes="minutes", :speed-ratio="speedRatio")
            v-expansion-panel
                v-expansion-panel-content
                    div(slot="header") Flight status
                    v-card
                        v-card-text.grey.lighten-3
                            | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.

            //v-card
                v-card-text
                    p(v-for="r in processedRotations") {{r.data.originIataCode}} {{r.data.destinationIataCode}} >>> {{r.flying}} >>> {{r.flyingMins}} {{r.flightTime}}


            #mapCanvas.static-map-holder.mt-5.mb-5
                p Map holder
</template>


<script>

    import { mapConfigurationFactory, generatePin, generateInfoWindow, generatePlaneSymbol } from './../../../services/map-config-service'
    import { getRotations } from './../../../services/rotation-service'
    import Snackbar from './../../../common/components/snackbar.vue'
    import WeeklyCountdown from './weekly-countdown'

    export default {
        components: {
            Snackbar,
            WeeklyCountdown
        },
        data () {
            return {
                name: '',
                color: '',
                snackBarText: '',
                snackBarShow: false,
                mapObject: null,
                rotations: [],
                rotationMarkers: [],
                airportsProcessed: {},
                color: '',
                animationStarted: false,
                speedRatio: 240,
                tickedId: null,
                minutesGone: 0,
                processedRotations: [],
                pathsOnMap: {},
                airportsProcessed: {}
            }
        },
        created() {
            if (this.$route.params.combinationId === undefined) {
                this.$router.push({name: 'combinations'})
            } else {
                this.color = this.$route.params.color
                this.name = this.$route.params.name
                getRotations(this.$route.params.combinationId).then(rsp => {
                    this.rotations = rsp
                    this.loadMap()
                    this.preProcessRotations(this.rotations)
                }).catch(err => {
                    this.snackBarText = 'Error loading rotations for combination'
                    this.snackBarShow = !this.snackBarShow
                })
            }
        },
        methods: {
            preProcessRotations() {
                this.processedRotations = []
                this.rotations.forEach(r => {
                    let _tComps = r.utcDepartureTime.split(":")
                    let departureMinutesFromMidnight = (Number(_tComps[0]) * 60) + Number(_tComps[1])
                    let _preparedRotation = {
                        'data': r,
                        'departureMinutes': departureMinutesFromMidnight,
                        'flightTime': r.flightTime,
                        '0': r.utcDayMap[1],
                        '1': r.utcDayMap[2],
                        '2': r.utcDayMap[3],
                        '3': r.utcDayMap[4],
                        '4': r.utcDayMap[5],
                        '5': r.utcDayMap[6],
                        '6': r.utcDayMap[7],
                        'flying': false,
                        'flyingMins' : 0
                    }


                    if (r.utcDayMap[7] === true) {

                        let timeUntilMidnight = 24 * 60 - departureMinutesFromMidnight
                        if (timeUntilMidnight < r.flightTime) {
                            _preparedRotation.flying = true
                            _preparedRotation.flyingMins = timeUntilMidnight
                            this.setIcon(_preparedRotation, true, (timeUntilMidnight / _preparedRotation.flightTime) * 100)
                        }

                    }

                    this.processedRotations.push(_preparedRotation)
                })
                this.drawAirports()
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
                    zoom: 3,
                    center: new google.maps.LatLng(51.5072, 0.1275),
                    styles: mapConfigurationFactory()
                };
                this.mapObject = new google.maps.Map(document.getElementById('mapCanvas'), options);
            },
            pingRotations() {
                let self = this
                this.processedRotations.forEach(r => {
                    if(r.flying === true) {
                        r.flyingMins ++
                        if (r.flyingMins === r.flightTime) {
                            r.flying = false
                            r.flyingMins = 0
                            self.removeIcon(r.data.id)
                        } else {
                            self.setIcon(r, false, ((r.flyingMins / r.flightTime) * 100))
                        }
                    }
                    if (r[self.day] === true) {
                        let _t = (self.day) * 60 * 24 + r.departureMinutes
                        if (self.minutesGone === _t) {
                            r.flying = true
                            self.setIcon(r, true, 0)
                        }
                    }
                })

            },
            animate() {
                // 10080 minutes in a week
                this.animationStarted = true
                let milisInMinute = 1000 * 60


                let self = this
                this.tickerId = setInterval(function () {

                    self.minutesGone++

                    console.log('minutes: ', self.minutesGone)

                    self.pingRotations()

                    if (self.minutesGone == 10080) {
                        self.stop()
                    }

                }, milisInMinute / this.speedRatio)
            },
            stop() {
                this.minutesGone = 0
                clearInterval(this.tickerId)
                this.animationStarted = false
                this.preProcessRotations()
            },
            pause() {
                clearInterval(this.tickerId)
                this.animationStarted = false
            },
            drawAirports() {
                this.processedRotations.forEach(pr => {
                    if (this.airportsProcessed[pr.data.originIataCode] === undefined) {
                        var markerOrigin = new google.maps.Marker({
                            position: {
                                lat: pr.data.originLatitude,
                                lng: pr.data.originLongitude
                            },
                            map: this.mapObject,
                            icon: generatePin(this.color.substring(1)),
                            title: pr.data.originName,
                            opacity: 0.6
                        });
                        this.airportsProcessed[pr.data.originIataCode] = true
                        markerOrigin.setMap(this.mapObject)

                        var _color = this.color

                        markerOrigin.addListener('click', function () {
                            generateInfoWindow(pr.data.originIataCode,
                                pr.data.originIcaoCode, pr.data.originName, pr.data.originCityName, pr.data.originCountry,
                                pr.data.originLongitude, pr.data.originLatitude, _color).open(this.mapObject, markerOrigin);
                        });
                    }


                    if (this.airportsProcessed[pr.data.destinationIataCode] === undefined) {

                        var markerDestination = new google.maps.Marker({
                            position: {
                                lat: pr.data.destinationLatitude,
                                lng: pr.data.destinationLongitude
                            },
                            map: this.mapObject,
                            icon: generatePin(this.color.substring(1)),
                            title: pr.data.destinationName,
                            opacity: 0.6
                        });

                        this.airportsProcessed[pr.data.destinationIataCode] = true
                        markerDestination.setMap(this.mapObject)

                        var _color = this.color


                        markerDestination.addListener('click', function () {
                            generateInfoWindow(pr.data.destinationIataCode,
                                pr.data.destinationIcaoCode, pr.data.destinationName, pr.data.destinationCityName, pr.data.destinationCountry,
                                pr.data.destinationLongitude, pr.data.destinationLatitude, _color).open(this.mapObject, markerDestination);
                        });
                    }
                })
            },
            setIcon(processedRoute, add, percentage) {
                if (add) {
                    var flightPlanCoordinates = [
                        {
                            lat: processedRoute.data.originLatitude,
                            lng: processedRoute.data.originLongitude
                        },
                        {
                            lat: processedRoute.data.destinationLatitude,
                            lng: processedRoute.data.destinationLongitude
                        }
                    ]
                    var trailPath = new google.maps.Polyline({
                        path: flightPlanCoordinates,
                        strokeColor: this.color,
                        strokeWeight: 2,
                        strokeOpacity: 1,
                        geodesic: true,
                        icons: [{
                            icon: generatePlaneSymbol(),
                            offset: percentage + '%'
                        }]
                    });

                    trailPath.setMap(this.mapObject)
                    this.pathsOnMap[processedRoute.data.id] = trailPath
                    console.log(this.pathsOnMap[processedRoute.data.id])
                } else {
                    if (this.pathsOnMap[processedRoute.data.id] !== undefined) {
                        this.pathsOnMap[processedRoute.data.id].icons[0].offset = percentage + '%'
                        this.pathsOnMap[processedRoute.data.id].strokeOpacity = 1
                        this.pathsOnMap[processedRoute.data.id].setPath(this.pathsOnMap[processedRoute.data.id].getPath())
                    }
                }
            },
            removeIcon(id) {
                this.pathsOnMap[id].setMap(null)
                this.pathsOnMap[id] = undefined
            }
        },
        computed: {
            day() {
                return Math.floor(this.minutesGone / 1440)
            },
            hours() {
                return Math.floor(((this.minutesGone % 1440) / 60))
            },
            minutes() {
                return ((this.minutesGone % 1440) % 60)
            }
        },
        destroy() {
            this.stop()
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