<template lang="pug">
    div
        v-data-table.elevation-2.maxed-height(:headers='headers', :items='carriers', hide-actions)
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
        }
    }
</script>


<style scoped>

</style>