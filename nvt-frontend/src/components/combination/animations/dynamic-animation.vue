<template lang="pug">
    div
        snackbar(:text="snackBarText", :show="snackBarShow")
        v-flex.mt-5(xs10, offset-xs1)
            h5 Dynamic network visualization

            v-card
                v-card-title Settings
                v-card-text
                    v-flex(xs12)
                        v-slider(v-model="speedRatio", :disabled="animationStarted", thumb-label, step="2", snap, max="100", min="1", label="Animation speed")
                v-card-actions
                    v-spacer
                    v-btn(color="orange", dark, @click="animate", v-if="!animationStarted") Start animation
                    v-btn(color="orange", dark, @click="stop", v-if="animationStarted") Stop animation


            #mapCanvas.static-map-holder.mt-5(v-if="animationStarted")
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
                color: '',
                animationStarted: false,
                speedRatio: 1,
                tickedId: null
            }
        },
        created() {
            if (this.$route.params.combinationId === undefined) {
                this.$router.push({name:'combinations'})
            } else {
                this.color = this.$route.params.color
                getRotations(this.$route.params.combinationId).then(rsp => {
                    this.rotations = rsp
                }).catch(err => {
                    this.snackBarText = 'Error loading rotations for combination'
                    this.snackBarShow = !this.snackBarShow
                })
            }
        },
        methods: {
            loadMap() {

                console.log('load map')

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
            },
            animate() {
                // 10080 minutes in a week
                console.log('animate called')
                this.animationStarted = true
                let milisInMinute = 1000 * 60
                let minutesGone = 0
                this.tickerId = setInterval(function () {
                    minutesGone++
                   console.log('called tm ', minutesGone)

                }, milisInMinute / this.speedRatio)
            },
            stop() {
                clearInterval(this.tickerId)
                this.animationStarted = false
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