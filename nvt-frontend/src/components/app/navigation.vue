<template lang="pug">
        v-toolbar(dark='', color='primary')

                v-toolbar-title.white--text.title(@click="$router.push({path: '/'})") Network Visualization Tool

                v-spacer

                v-btn(icon='')
                    v-icon search

                v-btn(icon)
                    v-icon local_airport

                v-btn(icon)
                    v-icon flight_land
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