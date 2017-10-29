<template lang="pug">
    v-dialog(v-model="dialog", persistent="", max-width="800px")
        v-btn(color="primary", dark, slot="activator") {{ label }}
        v-card
            v-card-title
                span.headline {{ label }}
            v-card-text
                v-container(grid-list-md)
                    v-layout(wrap)
                        v-flex(xs9)
                            v-text-field(label="Search criteria", v-model="searchCriteria", required, @keyup.enter="performSearch")
                        v-flex(xs3)
                            v-btn.mt-3(primary, @click="performSearch", :disabled="searchCriteria.length < 3") Search
                        v-flex(xs12)
                            span(v-if="airports !== undefined && airports.airportDtoList !== undefined && searchCriteria !== undefined") Showing {{airports.airportDtoList.length}}/{{airports.totalNum}} for search criteria '{{ criteriaUsed }}'
                            v-data-table.elevation-1(:headers='headers', :items='airports.airportDtoList', hide-actions)
                                template(slot='items', slot-scope='props')

                                    td {{props.item.iataCode}}
                                        |
                                        span(v-if="props.item.icaoCode !== undefined && props.item.icaoCode !=='\\\\N'") /{{props.item.icaoCode}}

                                    td {{props.item.name}}

                                    td {{props.item.city}} ({{props.item.country}})

                                    td
                                        v-btn(color="primary", @click.native="returnAction(props.item)") {{action}}

            v-card-actions
                v-spacer
                v-btn(color="blue darken-1", flat, @click.native="closeDialog") Close

</template>


<script>

    import { partialSearch } from './../../../services/airport-service'

    export default {
        props: ['label', 'action'],
        data () {
            return {
                dialog: false,
                searchCriteria: '',
                criteriaUsed: '',
                airports: {
                    totalNum: 0,
                    airportDtoList: []
                },
                headers: [
                    {
                        text: 'Code (IATA/ICAO)',
                        align: 'left',
                        sortable: true,
                        value: 'Code'
                    },
                    { text: 'Name', value: 'name', align: 'left', sortable: true },
                    { text: 'Location', value: 'location', align: 'left', sortable: false  }
                ]
            }
        },
        methods: {
            closeDialog() {
                this.dialog = false
            },
            performSearch() {
                if (this.searchCriteria.length < 3) {
                    return
                }
                this.criteriaUsed = this.searchCriteria
                partialSearch(this.searchCriteria).then(rsp => {
                    console.log(rsp)
                    this.airports = rsp
                }).catch(err => {
                    this.airports = {
                        totalNum: 0,
                        airportDtoList: []
                    }
                })
            },
            returnAction(item) {
                this.$emit('return-action', item)
                this.searchCriteria = ''
                this.criteriaUsed = ''
                this.airports = {
                    totalNum: 0,
                    airportDtoList: []
                }
                this.dialog = false
            }
        }
    }
</script>


<style scoped>

</style>