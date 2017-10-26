<template lang="pug">
    v-dialog(v-model="dialog", persistent="", max-width="800px")
        v-btn(color="orange", dark, slot="activator", :disabled="disableModal")
                v-icon(dark, left) flight
                | {{ label }}
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
                                span(v-if="searchCriteria !== undefined && airlines !== undefined && airlines.airlineDtoList !== undefined") Showing {{airlines.airlineDtoList.length}}/{{airlines.totalNum}} for search criteria '{{ criteriaUsed }}'
                                v-data-table.elevation-1(:headers='headers', :items='airlines.airlineDtoList', hide-actions)
                                    template(slot='items', slot-scope='props')

                                        td {{props.item.iataCode}}
                                            |
                                            span(v-if="props.item.icaoCode !== undefined && props.item.icaoCode !== '' && props.item.icaoCode !=='\\\\N'") /{{props.item.icaoCode}}

                                        td {{props.item.name}}

                                        td {{props.item.country}}

                                        td
                                            v-btn(color="primary", @click.native="returnAction(props.item)") {{action}}
                v-card-actions
                    v-spacer
                    v-btn(color="blue darken-1", flat, @click.native="closeDialog") Close

</template>


<script>

    import { partialSearch } from './../../services/airline-service'


    export default {
        props: ['label', 'action', 'disable-modal'],
        data () {
            return {
                dialog: false,
                searchCriteria: '',
                criteriaUsed: '',
                airlines: {
                    totalNum: 0,
                    airlineDtoList: []
                },
                headers: [
                    {
                        text: 'Code (IATA/ICAO)',
                        align: 'left',
                        sortable: true,
                        value: 'Code'
                    },
                    { text: 'Name', value: 'name', align: 'left', sortable: true },
                    { text: 'Country', value: 'country', align: 'left', sortable: false  }
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
                    this.airlines = rsp
                }).catch(err => {
                    this.airlines = {
                        totalNum: 0,
                        airlineDtoList: []
                    }
                })
            },
            returnAction(item) {
                this.$emit('selected-airline', item)
                this.searchCriteria = ''
                this.criteriaUsed = ''
                this.airlines = {
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