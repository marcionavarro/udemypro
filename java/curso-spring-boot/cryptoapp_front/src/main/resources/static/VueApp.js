const {createApp} = Vue

const baseUrl = 'http://localhost:8080/coin'

const mainContainer = {
    data() {
        return {
            coins: [],
            canSeeCoins: true,
            canSeeTransactions: false,
            formCoin: {
                isNew: true,
                name: '',
                price: '',
                quantity: '',
                title: 'Cadastrar nova transação',
                button: 'Cadastrar'
            },
            transactions: []
        }
    },
    mounted() {
        this.showAllCoins()
    },
    methods: {
        showAllCoins() {
            this.coins = []
            axios.get(baseUrl)
                .then(res => {
                    if (res.data.length <= 0) {
                        this.canSeeCoins = false
                    }
                    res.data.map((item) => this.coins.push(item))
                })
                .catch(err => console.log(err))
        },
        showTransactions(name) {
            this.formCoin = {
                isNew: true,
                name: '',
                price: '',
                quantity: '',
                title: 'Cadastrar nova transação',
                button: 'Cadastrar'
            },

                this.transactions = {
                    coinName: name,
                    data: []
                }

            this.canSeeTransactions = true

            axios.get(`${baseUrl}/${name}`)
                .then(res => {
                    if (res.data.length <= 0) {
                        this.canSeeTransactions = false
                    }
                    res.data.map(item => {
                        this.transactions.data.push({
                            id: item.id,
                            name: item.name,
                            price: item.price.toLocaleString('pt-br', {
                                style: 'currency',
                                currency: 'BRL'
                            }),
                            quantity: item.quantity,
                            dateTime: this.formattedDate(item.dateTime)
                        })
                    })
                })
                .catch(err => toastr.error(err, this.toastrOptions()))
        },
        saveCoin() {
            this.formCoin.name = this.formCoin.name.toUpperCase()
            this.formCoin.price = this.formCoin.price.replace('R$', '')
                .replace(',', '.').trim()

            if (this.formCoin.name === '' || this.formCoin.price === '' || this.formCoin.quantity === '') {
                toastr.error('Todos os campos são obrigatórios', 'Formulário', this.toastrOptions())
                return
            }

            const coin = {
                name: this.formCoin.name,
                price: this.formCoin.price,
                quantity: this.formCoin.quantity
            }

            const self = this

            if (this.formCoin.isNew) {
                this.coins = []
                this.canSeeCoins = true
                axios.post(baseUrl, coin)
                    .then(() => {
                        toastr.success('Nova transação cadastrada com sucesso!', 'Formulário', self.toastrOptions())
                    })
                    .catch(err => toastr.error(`Não foi possivel cadastrar uma nova transação.${err}`, 'Formulário', self.toastrOptions()))
                    .then(() => self.thenFinally(self))
                return
            }

            const putCoin = {
                id: this.formCoin.id,
                ...coin
            }

            axios.put(baseUrl, putCoin)
                .then(() => toastr.success('Transação atualizada com sucesso!', 'Formulário', self.toastrOptions()))
                .catch(err => toastr.error(`Não foi possível atualizar a transação. ${err}`, 'Formulário', self.toastrOptions()))
                .then(() => self.thenFinally(self))
        },
        editTransaction(transaction) {
            this.formCoin = {
                isNew: false,
                id: transaction.id,
                name: transaction.name.toUpperCase(),
                price: transaction.price,
                quantity: transaction.quantity,
                title: 'Editar transação',
                button: 'Atualizar'
            }
        },
        removeTransaction(transaction) {
            const self = this

            axios.delete(`${baseUrl}/${transaction.id}`)
                .then(() => toastr.success('Transação removida com sucesso!', 'Exclusão', self.toastrOptions()))
                .catch(err => toastr.error(`Não foi possível remover as transações. ${err}`, 'Exclusão', self.toastrOptions()))
                .then(() => {
                    self.showAllCoins()
                    self.showTransactions(transaction.name)
                    self.cleanForm()
                })

        },
        cleanForm() {
            this.formCoin.isNew = true,
                this.formCoin.name = '',
                this.formCoin.price = '',
                this.formCoin.quantity = '',
                this.formCoin.title = 'Cadastrar nova transação',
                this.formCoin.button = 'Cadastrar'
        },
        thenFinally(self) {
            self.showAllCoins()
            self.showTransactions(self.formCoin.name)
            self.cleanForm()
        },
        formattedDate(date) {
            return (new Date(date.split('T')[0])).toLocaleDateString("pt-br")
        },
        toastrOptions() {
            return {
                timeOut: 5000,
                positionClass: 'toast-top-left'
            }
        }
    }
}

createApp(mainContainer).mount('#app')