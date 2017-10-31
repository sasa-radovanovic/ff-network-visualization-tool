<template lang="pug">
    div
        v-data-table.elevation-2.maxed-height(:headers='headers', :items='operatingCarriers', hide-actions)
            template(slot='items', slot-scope='props')

                td {{props.item.iataCode}} / {{props.item.icaoCode}}

                td {{props.item.name}}

                td {{props.item.country}}


</template>


<script>
    export default {
        props: ['carriers'],
        data () {
            return {
                operatingCarriers: [],
                headers: [
                    {
                        text: 'Code',
                        align: 'left',
                        sortable: true,
                        value: 'iataCode'
                    },
                    { text: 'Name', value: 'name', align: 'left', sortable: true },
                    { text: 'Country', value: 'country', align: 'left', sortable: true  }
                ]
            }
        },
        created() {
            this.operatingCarriers = Object.values(this.carriers)
        },
        watch: {
            carriers(value) {
                this.operatingCarriers = Object.values(value)
            }
        }
    }
</script>


<style scoped>

</style>