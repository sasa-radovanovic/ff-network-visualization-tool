<template lang="pug">
    div
        v-flex.mt-5(xs8, offset-xs2)
            v-card
                v-card-media(src="/static/card-header-1.png", height="200px")
                v-card-title(primary-title)
                    h5 Combinations
                    v-data-table.elevation-1(:headers='headers', :items='combinations', hide-actions)
                        template(slot='items', slot-scope='props')

                            td {{props.item.name}}

                            td {{props.item.name}}

                            td
                                v-btn(color="primary", :to="{name: 'edit-combination', params: {combinationId: props.item.id}}") Edit
                                v-btn(color="primary", :to="{name: 'static-animation', params: {combinationId: props.item.id}}") Static display
                                v-btn(color="primary", :to="{name: 'dynamic-animation', params: {combinationId: props.item.id}}") Animate

                v-card-actions
                    v-spacer
                    v-btn.mt-5(color="orange", flat, :to="{name:  'new-combination'}") Create new combination

</template>


<script>

    import { getCombinations } from './../../services/combination-service'


    export default {
        data() {
            return {
                msg: 'Combinations',
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
            getCombinations().then(rsp => {
                console.log('COMBS ARE HERE', rsp)
                this.combinations = rsp
            })
        },
        methods: {
            edit(x, y) {
                console.log(x)
                console.log(y)
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