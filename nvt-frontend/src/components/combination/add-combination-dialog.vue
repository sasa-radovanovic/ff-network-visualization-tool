<template lang="pug">
    v-dialog(v-model="dialog", persistent="", max-width="800px")
        v-btn(color="orange", dark, slot="activator", @click="loadCombinations", :disabled="disableModal")
            v-icon(dark, left) add_location
            | {{ label }}
        v-card
            v-card-title
                span.headline {{ label }}
            v-card-text
                v-container(grid-list-md)
                    v-layout(wrap)
                        v-flex(xs12)
                            v-data-table.elevation-1(:headers='headers', :items='combinations', hide-actions)
                                template(slot='items', slot-scope='props')

                                    td {{props.item.name}}

                                    td
                                        v-icon(large, :style="{'color' : props.item.color}") flight_land

                                    td
                                        v-btn(color="primary", @click.native="returnAction(props.item)") {{action}}
            v-card-actions
                v-spacer
                v-btn(color="blue darken-1", flat, @click.native="closeDialog") Close
</template>


<script>
    import { getCombinations } from './../../services/combination-service'


    export default {
        props: ['label', 'action', 'disable-modal'],
        data () {
            return {
                dialog: false,
                headers: [
                    {
                        text: 'Name',
                        align: 'left',
                        sortable: false,
                        value: 'Name'
                    },
                    { text: 'Color', value: 'color', align: 'left', sortable: false },
                    { text: 'Actions', value: 'actions', align: 'left', sortable: false  }
                ],
                combinations: []
            }
        },
        methods: {
            loadCombinations() {
                getCombinations().then(rsp => {
                    this.combinations = rsp
                }).catch(err => {
                    this.combinations = []
                })
            },
            closeDialog() {
                this.dialog = false
            },
            returnAction(item) {
                this.$emit('selected-combination', item)
                this.dialog = false
            }
        }
    }
</script>


<style scoped>

</style>