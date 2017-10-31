<template lang="pug">
    v-layout(row, justify-center)
            v-card
                v-card

                    v-card-text
                        v-layout(row, wrap)
                            v-flex(xs6)
                                v-card
                                    v-card-media()
                                    v-card-title
                                        h5 {{basicData1.name}} [{{basicData1.iataCode}} / {{basicData1.icaoCode}}]
                                    v-card-title {{basicData1.city}}, {{basicData1.country}}
                            v-flex(xs6)
                                v-card
                                    v-card-media()
                                    v-card-title
                                        h5 {{basicData2.name}} [{{basicData2.iataCode}} / {{basicData2.icaoCode}}]
                                    v-card-title {{basicData2.city}}, {{basicData2.country}}
                            v-flex(xs12).mt-5
                                p Routes from airport ({{basicData1.iataCode}} = {{detailedData1.connections.length}} vs. {{basicData2.iataCode}} = {{detailedData2.connections.length}})
                                v-progress-linear(:value="connectionValue", height="20", color="info", background-color="orange")

                            v-flex(xs12).mt-2
                                p Operating carriers from airport ({{basicData1.iataCode}} = {{Object.values(detailedData1.operatingCarriers).length}} vs. {{basicData2.iataCode}} = {{Object.values(detailedData2.operatingCarriers).length}})
                                v-progress-linear(:value="carriersValue", height="20", color="info", background-color="orange")

                            v-flex(xs12).mt-2
                                v-layout(row, wrap)
                                    v-flex(xs4)

                                        v-card
                                            v-toolbar(color="info", dark)
                                                v-toolbar-title.toolbar-carriers-title Carriers operating from {{basicData1.iataCode}} only
                                                v-spacer
                                            v-list(two-line)
                                                template(v-for="(item, index) in Object.values(sortedCarriers[basicData1.iataCode])")
                                                    v-list-tile(avatar, ripple, :key="item.uniqueId")
                                                        v-list-tile-content
                                                            v-list-tile-title {{item.name}} [{{item.iataCode}} / {{item.icaoCode}}]
                                                            v-list-tile-sub-title {{item.country}}
                                                    v-divider(v-if="index + 1 < sortedCarriers[basicData1.iataCode].length", :key="item.uniqueId")
                                    v-flex(xs4)

                                        v-card
                                            v-toolbar.toolbar-carriers-gray(dark)
                                                v-toolbar-title.toolbar-carriers-title Carriers operating from both
                                                v-spacer

                                            v-list(two-line)
                                                template(v-for="(item, index) in Object.values(sortedCarriers['both'])")
                                                    v-list-tile(avatar, ripple, :key="item.uniqueId")
                                                        v-list-tile-content
                                                            v-list-tile-title {{item.name}} [{{item.iataCode}} / {{item.icaoCode}}]
                                                            v-list-tile-sub-title {{item.country}}
                                                    v-divider(v-if="index + 1 < sortedCarriers['both'].length", :key="item.uniqueId")
                                    v-flex(xs4)
                                        v-card
                                            v-toolbar.toolbar-carriers-orange(dark)
                                                v-toolbar-title.toolbar-carriers-title Carriers operating from {{basicData2.iataCode}} only
                                                v-spacer
                                            v-list(two-line)
                                                template(v-for="(item, index) in Object.values(sortedCarriers[basicData2.iataCode])")
                                                    v-list-tile(avatar, ripple, :key="item.uniqueId")
                                                        v-list-tile-content
                                                            v-list-tile-title {{item.name}} [{{item.iataCode}} / {{item.icaoCode}}]
                                                            v-list-tile-sub-title {{item.country}}
                                                    v-divider(v-if="index + 1 < sortedCarriers[basicData2.iataCode].length", :key="item.uniqueId")

</template>


<script>

    import { airportDetails } from './../../services/airport-service'


    export default {
        data () {
            return {
                basicData1: {},
                detailedData1: {},
                basicData2: {},
                dialog: false,
                detailedData2: {},
                sortedCarriers: {},
                dataPrepared: false
            }
        },
        methods: {
            loadData() {
                airportDetails(this.basicData2.iataCode).then(rsp => {
                    this.detailedData2 = rsp
                    this.sortCarriers()
                })
            },
            sortCarriers() {

                this.dataPrepared = false

                this.sortedCarriers[this.basicData2.iataCode] = {}
                this.sortedCarriers[this.basicData1.iataCode] = {}
                this.sortedCarriers['both'] = {}

                let self = this

                Object.keys(this.detailedData1.operatingCarriers).forEach(function (uniqueId) {

                    if (self.detailedData2.operatingCarriers[uniqueId] !== undefined) {
                        self.sortedCarriers['both'][uniqueId]  = {}
                        self.sortedCarriers['both'][uniqueId]  = self.detailedData1.operatingCarriers[uniqueId]
                    } else {
                        self.sortedCarriers[self.basicData1.iataCode][uniqueId] = {}
                        self.sortedCarriers[self.basicData1.iataCode][uniqueId]  = self.detailedData1.operatingCarriers[uniqueId]
                    }

                })

                Object.keys(this.detailedData2.operatingCarriers).forEach(function (uniqueId) {
                    if (self.sortedCarriers['both'][uniqueId] === undefined) {
                        self.sortedCarriers[self.basicData2.iataCode][uniqueId] = {}
                        self.sortedCarriers[self.basicData2.iataCode][uniqueId]  = self.detailedData2.operatingCarriers[uniqueId]
                    }
                })

                this.dataPrepared = true
            }
        },
        created() {
            if (this.$route.params.basicData1 === undefined || this.$route.params.basicData2 === undefined) {
                this.$router.push({'name' : 'airport-data'})
            } else {
                this.basicData1 = this.$route.params.basicData1
                this.basicData2 = this.$route.params.basicData2
                this.detailedData1 = this.$route.params.detailedData1
                console.log('params prepared')
                this.loadData()
            }
        },
        watch: {

        },
        computed: {
            connectionValue() {
                return (this.detailedData1.connections.length / (this.detailedData1.connections.length + this.detailedData2.connections.length)) * 100
            },
            carriersValue() {
                return (Object.keys(this.detailedData1.operatingCarriers).length / (Object.keys(this.detailedData1.operatingCarriers).length + Object.keys(this.detailedData2.operatingCarriers).length)) * 100
            }
        }
    }
</script>


<style scoped>
    .toolbar-carriers-orange {
        background-color: orange !important;
        border-color: orange !important;
    }
    .toolbar-carriers-gray {
        background-color: darkgrey !important;
        border-color: darkgrey !important;
    }
    .toolbar-carriers-title {
        font-size: 16px !important;
    }
</style>