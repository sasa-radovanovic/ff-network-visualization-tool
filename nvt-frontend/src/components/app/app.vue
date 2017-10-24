<template lang="pug">
    v-app.home
        busy-spinner
        navigation
        router-view
</template>

<script>
    import BusySpinner from '../../common/components/busy-spinner.vue'

    import Navigation from './navigation.vue'

    export default {
        components: {
            BusySpinner,
            Navigation
        }
    }

</script>

<style>
    .firstHeading {
        font-size: 20px;
    }
</style>
