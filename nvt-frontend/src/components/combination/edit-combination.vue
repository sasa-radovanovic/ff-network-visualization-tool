<template lang="pug">
    div
        snackbar(:text="toasterText", :show="showSnackbar")
        v-flex.mt-5.mb-5(xs8, offset-xs2)
            v-card
                v-card-media(:style="{'background-color' : $route.params.color}", height="50px")
                v-card-title(primary-title)
                    h5 {{this.$route.params.name}}
                v-card-text
                    combination-rotations(:combinationId="$route.params.combinationId")
</template>


<script>

    import CombinationRotations from './rotations/combination-rotations.vue'
    import Snackbar from './../../common/components/snackbar.vue'
    export default {
        components: {
            CombinationRotations,
            Snackbar
        },
        data() {
            return {
                msg: 'This is edit',
                toasterText: '',
                showSnackbar: false
            }
        },
        created() {
            if (this.$route.params.combinationId === undefined
                || this.$route.params.color === undefined
                || this.$route.params.name === undefined) {
                this.$router.push({name:'combinations'})
            }
        }
    }

</script>


<style scoped>

</stylescoped>