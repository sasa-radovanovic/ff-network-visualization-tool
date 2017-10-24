<template lang="pug">
    v-snackbar(:timeout="timeout",
    :top="y === 'top'",
    :bottom="y === 'bottom'",
    :right="x === 'right'",
    :left="x === 'left'",
    :multi-line="mode === 'multi-line'",
    :vertical="mode === 'vertical'",
    v-model="snackbar")
        | {{ text }}
        v-btn(flat, color="pink", @click.native="snackbar = false") Close
</template>


<script>
    export default {
        props: ['text', 'show'],
        data () {
            return {
                snackbar: false,
                y: 'top',
                x: null,
                mode: '',
                timeout: 4000
            }
        },
        watch: {
            show(val) {
                console.log('show it')
                this.snackbar = true
            }
        }
    }
</script>


<style scoped>

</style>