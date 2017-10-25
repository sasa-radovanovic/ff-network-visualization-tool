<template lang="pug">
    div
        v-flex.mt-5.mb-5(xs8, offset-xs2)
            v-card
                v-card-media(src="/static/card-header-4.png", height="200px")
                v-card-title(primary-title)
                    h5 Airline Search
                v-card-text
                    v-layout(wrap)
                        v-flex(xs6)
                            v-text-field(label="Search criteria", v-model="searchCriteria", required, @keyup.enter="performSearch")
                        v-flex(xs3)
                            v-btn.mt-3(primary, color="orange", @click="performSearch", :disabled="searchCriteria === undefined || searchCriteria.length < 3") Search
                        v-flex(xs12)
                            span(v-if="searchCriteria !== undefined") Showing {{airlines.airlineDtoList.length}}/{{airlines.totalNum}} for search criteria '{{ criteriaUsed }}'
                            v-data-table.elevation-1(:headers='headers', :items='airlines.airlineDtoList', hide-actions)
                                template(slot='items', slot-scope='props')

                                    td {{props.item.iataCode}}
                                        |
                                        span(v-if="props.item.icaoCode !== undefined && props.item.icaoCode !== '' && props.item.icaoCode !=='\\\\N'") /{{props.item.icaoCode}}

                                    td {{props.item.name}}

                                    td {{props.item.country}}

                                    td
                                        v-btn(color="orange", dark, :to="{name: 'airline-network', params: {airline: props.item}}")
                                            v-icon(dark, left) timeline
                                            | Network

</template>


<script>

    import { partialSearch } from './../../services/airline-service'
    import Snackbar from './../../common/components/snackbar'

    export default {
        data() {
            return {
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
            }
        }
    }
</script>