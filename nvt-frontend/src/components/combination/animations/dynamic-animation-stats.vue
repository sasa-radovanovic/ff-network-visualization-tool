<template lang="pug">
    div
        p Flights in the air: {{filteredRotations.length}}
        v-data-table.elevation-2.maxed-height(:headers='headers', :items='filteredRotations', hide-actions)
            template(slot='items', slot-scope='props')

                td {{props.item.data.originIataCode}}

                td {{props.item.data.destinationIataCode}}

                td {{props.item.flyingMins}} / {{props.item.flightTime}}

                td {{Math.floor(props.item.flyingMins/props.item.flightTime * 100)}} %

                td {{(props.item.flightTime - props.item.flyingMins)}}
</template>


<script>
    export default {
        props: ['rotations'],
        data () {
            return {
                headers: [
                    {
                        text: 'Origin',
                        align: 'left',
                        sortable: false,
                        value: 'Origin'
                    },
                    { text: 'Destination', value: 'originIataCode', align: 'left', sortable: false },
                    { text: 'In air [min]/ Total flight time [min]', value: 'flightTime', align: 'left', sortable: false  },
                    { text: 'Percentage flown', value: 'perc', align: 'left', sortable: false  },
                    { text: 'Time to destination [min]', value: 'timeToDest', align: 'left', sortable: false  }
                ]
            }
        },
        computed: {
            filteredRotations() {
                let filtered =  this.rotations.filter(r => {
                    return r.flying === true
                })
                return filtered
            }
        }
    }
</script>


<style scoped>
    .maxed-height {
        max-height: 200px;
        overflow: scroll;
    }
</style>