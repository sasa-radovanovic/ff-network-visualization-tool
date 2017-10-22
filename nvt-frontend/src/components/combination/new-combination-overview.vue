<template lang="pug">
    div.overview
        h5 New combination overview
        v-layout(row, wrap)
            v-flex(xs6)
                v-text-field(
                label="Name",
                v-model="name",
                disabled
                )
            v-flex(xs3)
                v-text-field(
                label="Color",
                v-model="color",
                disabled
                )
            v-flex.text-xs-center.pt-3(xs3)
                span.mt-5
                    v-icon(large, :style="{'color' : color}") flight_land
            v-flex(xs12)
                h5 Rotations
                v-data-table.elevation-1(:headers='headers', :items='rotations', hide-actions)
                    template(slot='items', slot-scope='props')

                        td {{props.item.origin}}

                        td {{props.item.destination}}

                        td {{props.item.frequency}}

                        td {{props.item.departureTime}}

                        td {{props.item.flightLength}}
</template>


<script>
    export default {
        props:['rotations', 'name', 'color'],
        data() {
            return {
                headers: [
                    {
                        text: 'Origin',
                        align: 'left',
                        sortable: true,
                        value: 'Origin'
                    },
                    {
                        text: 'Destination',
                        align: 'left',
                        sortable: true,
                        value: 'Destination'
                    },
                    {
                        text: 'Frequency',
                        align: 'left',
                        sortable: true,
                        value: 'Frequency'
                    },
                    {
                        text: 'Departure time',
                        align: 'left',
                        sortable: true,
                        value: 'Departure time'
                    },
                    {
                        text: 'Flight length [min]',
                        align: 'left',
                        sortable: true,
                        value: 'Flight length'
                    }
                ]
            }
        },
        methods: {

        },
        computed: {

        }
    }

</script>


<style scoped>
    .overview {
        padding: 20px;
    }
</style>