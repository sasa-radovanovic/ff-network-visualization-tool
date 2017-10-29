<template lang="pug">
        v-toolbar(dark='', color='primary')

                v-toolbar-title.white--text.title(@click="$router.push({path: '/'})") Network Visualization Tool

                v-spacer

                v-tooltip(bottom)
                    v-btn(icon, slot="activator", :to="{name: 'airlines'}")
                        v-icon local_airport
                    span Airlines

                v-tooltip(bottom)
                    v-btn(icon, slot="activator", :to="{name: 'airport-data'}")
                        v-icon flight_land
                    span Airports
</template>


<script>
    export default {

        data() {
            return {

            }
        },
        methods: {
            collapse() {

            }
        }

    }

</script>


<style scoped>
    .title:HOVER {
        cursor: pointer;
    }
</style>