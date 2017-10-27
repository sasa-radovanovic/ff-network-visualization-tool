<template lang="pug">
    div
        snackbar(:text="text", :show="showSnackbar")
        v-flex.mt-5(xs8, offset-xs2)
            v-card
                v-card-media(src="/static/card-header-1.png", height="200px")
                v-card-title(primary-title)
                    h5 Combinations
                    v-data-table.elevation-1(:headers='headers', :items='combinations', hide-actions)
                        template(slot='items', slot-scope='props')

                            td {{props.item.name}}

                            td
                                v-icon(large, :style="{'color' : props.item.color}") flight_land

                            td
                                v-btn(color="primary",
                                dark,
                                :to="{name: 'edit-combination', params: {combinationId: props.item.id, name: props.item.name, color: props.item.color}}")
                                    v-icon(dark, left) mode_edit
                                    | Edit
                                v-btn(color="orange", dark, :to="{name: 'static-animation', params: {combinationId: props.item.id, color: props.item.color}}")
                                    v-icon(dark, left) timeline
                                    | Static display
                                v-btn(color="orange", dark, :to="{name: 'dynamic-animation', params: {combinationId: props.item.id, color: props.item.color, name: props.item.name}}")
                                    v-icon(dark, left) cached
                                    | Animate
                                v-btn(color="red", dark, @click="removeCombination(props.item.id)")
                                    v-icon(dark, left) delete
                                    | Remove

                v-card-actions
                    v-spacer
                    v-btn.mt-5(color="orange", flat, :to="{name:  'new-combination'}") Create new combination

</template>


<script>

    import { getCombinations, removeRotation } from './../../services/combination-service'
    import Snackbar from './../../common/components/snackbar.vue'

    export default {
        components: {
            Snackbar
        },
        data() {
            return {
                text: '',
                showSnackbar: false,
                combinations: [],
                headers: [
                    {
                        text: 'Name',
                        align: 'left',
                        sortable: false,
                        value: 'Name'
                    },
                    { text: 'Color', value: 'color', align: 'left', sortable: false },
                    { text: 'Actions', value: 'actions', align: 'left', sortable: false  }
                ]
            }
        },
        created() {
            this.retrieveCombinations()
        },
        mounted() {
            if (this.$route.params.newCombination !== undefined) {
                this.text = "New combination successfully added"
                this.showSnackbar = !this.showSnackbar
            }
        },
        methods: {
            retrieveCombinations() {
                getCombinations().then(rsp => {
                    console.log('COMBS ARE HERE', rsp)
                    this.combinations = rsp
                })
            },
            testIt() {
                this.text = "New combination successfully added"
                this.showSnackbar = !this.showSnackbar
            },
            removeCombination(id) {
                removeRotation(id).then(rsp => {
                    this.text = "Combination successfully removed"
                    this.showSnackbar = !this.showSnackbar
                    this.retrieveCombinations()
                }).catch(err => {
                    this.text = "Error occured while removing combination"
                    this.showSnackbar = !this.showSnackbar
                })
            }
        }
    }
</script>


<style scoped lang="scss">
    h1 {
        p {
            color: red;
        }
    }

    .combinations {
        background: #ffc966;
    }

    .container-fluid {
        height: 100%;
    }
</style>