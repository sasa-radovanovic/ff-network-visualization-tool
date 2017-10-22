<template lang="pug">
    h4 Edit page
</template>


<script>
    export default {
        data() {
            return {
                msg: 'This is edit'
            }
        },
        created() {
            alert(this.$route.params.combinationId)
        }
    }

</script>


<style scoped>

</stylescoped>